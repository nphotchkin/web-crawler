package com.crawler.infrastructure.adapters.job.handler;

import com.crawler.commons.JitterGenerator;
import com.crawler.infrastructure.adapters.http.HttpClientException;
import com.crawler.infrastructure.adapters.crawler.CrawlerLinkExtractor;
import com.crawler.infrastructure.adapters.crawler.report.FailedCrawlEvent;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

public class ExtractLinksForDomainJobRequestRequestHandler implements JobRequestHandler<PageCrawlResult> {

    private final JitterGenerator jitterGenerator;
    private final URI uri;
    private final CrawlerLinkExtractor crawlerLinkExtractor;
    private final Set<String> bannedPaths;

    public ExtractLinksForDomainJobRequestRequestHandler(
            JitterGenerator jitterGenerator,
            CrawlerLinkExtractor crawlerLinkExtractor,
            URI uri,
            Set<String> bannedPaths
    ) {
        this.jitterGenerator = jitterGenerator;
        this.crawlerLinkExtractor = crawlerLinkExtractor;
        this.uri = uri;
        this.bannedPaths = bannedPaths;
    }

    @Override
    public PageCrawlResult perform() {
        jitterGenerator.waitAndJitter();
        Set<FailedCrawlEvent> failedCrawlEvents = new HashSet<>();
        Set<URI> validatedExtractedUris = new HashSet<>();
        try {
            var linkExtractionResult = crawlerLinkExtractor.extractLinksMatchingHostIgnoringBanned(
                    uri, uri.getPath(), bannedPaths
            );
            if (!linkExtractionResult.malformedLinks().isEmpty()) {
                failedCrawlEvents.add(FailedCrawlEvent.ofMalformedUri(uri.toString(), linkExtractionResult.malformedLinks()));
            }
            if (!linkExtractionResult.validExtractedUris().isEmpty()) {
                validatedExtractedUris.addAll(linkExtractionResult.validExtractedUris());
            }
        } catch (HttpClientException e) {
            failedCrawlEvents.add(FailedCrawlEvent.ofHttpError(uri.toString(), e.getMessage()));
        } catch (RuntimeException e) {
            failedCrawlEvents.add(FailedCrawlEvent.ofUnexpectedException(uri.toString(), e));
        }

        return new PageCrawlResult(uri, failedCrawlEvents, validatedExtractedUris);
    }

    @Override
    public String describeTask() {
        var thisClassName = ExtractLinksForDomainJobRequestRequestHandler.class.getSimpleName();
        return "'%s' execute for URI: '%s'".formatted(thisClassName, uri);
    }

}
