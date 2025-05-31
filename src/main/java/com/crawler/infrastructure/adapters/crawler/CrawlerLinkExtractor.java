package com.crawler.infrastructure.adapters.crawler;

import com.crawler.application.exception.WebCrawlerException;
import com.crawler.infrastructure.adapters.http.BasicHttpClient;
import com.crawler.infrastructure.adapters.http.HttpClientException;
import com.crawler.infrastructure.adapters.http.response.HttpResponseLinkExtractor;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.Set;

@Slf4j
public class CrawlerLinkExtractor {

    private final BasicHttpClient client;
    private final HttpResponseLinkExtractor linkExtractor;

    public CrawlerLinkExtractor(BasicHttpClient client, HttpResponseLinkExtractor linkExtractor) {
        this.client = client;
        this.linkExtractor = linkExtractor;
    }

    public URILinkExtractionResult extractLinksMatchingHostIgnoringBanned(
            URI baseDomain, String path, Set<String> bannedPaths
    ) {
        if (path == null) throw WebCrawlerException.invalidArguments("Path must not be null");
        HttpResponse<String> response;
        try {
            var contentType = client.getContentType(path);
            if (contentType.isEmpty() || !contentType.get().contains("text/html")) {
                log.debug(
                        "skipped extract links for path: '%s', content type: 'text/html' indicates its not a webpage."
                                .formatted(path)
                );
                return aLinkExtractionResultHavingNoValidLinks(baseDomain);
            }
            response = client.getAsString(path);
        } catch (HttpClientException e) {
            if (e.isRetryable()) {
                log.warn("NOT-IMPLEMENTED: A retryable exception occurred it could have been retried on exponential backoff.");
            }
            throw e;
        }
        if (response == null) throw WebCrawlerException.shouldNeverHappen("Response should not be null.");

        var linkExtractionResult = linkExtractor.extractLinksHavingSameDomain(
                baseDomain.resolve(path), response, bannedPaths
        );

        return new URILinkExtractionResult(
            baseDomain, linkExtractionResult.malformedPaths(), linkExtractionResult.validUris()
        );
    }

    private URILinkExtractionResult aLinkExtractionResultHavingNoValidLinks(URI baseDomain) {
        return new URILinkExtractionResult(
                baseDomain, Collections.emptySet(), Collections.emptySet()
        );
    }

}
