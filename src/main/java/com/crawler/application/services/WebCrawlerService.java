package com.crawler.application.services;

import com.crawler.application.exception.WebCrawlerException;
import com.crawler.application.usecase.CrawlSameDomainUseCase;
import com.crawler.infrastructure.adapters.crawler.WebCrawlerAdapter.WebCrawlerAdapterFactory;
import com.crawler.infrastructure.adapters.crawler.report.CrawlerReport;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.util.Set;

@Slf4j
public class WebCrawlerService implements CrawlSameDomainUseCase {

    private final WebCrawlerAdapterFactory webCrawlerAdapterFactory;

    public WebCrawlerService(WebCrawlerAdapterFactory webCrawlerAdapterFactory) {
        this.webCrawlerAdapterFactory = webCrawlerAdapterFactory;
    }

    @Override
    public CrawlerReport findAllUrlsForSameDomain(URI uri, Set<String> bannedPaths, int limit) {
        isValidUriOrElseThrowEx(uri);
        var webCrawler = webCrawlerAdapterFactory.defaultConfig(uri, bannedPaths, limit);
        return webCrawler.crawlWebsite();
    }

    private void isValidUriOrElseThrowEx(URI uri) {
        if (uri == null) {
            throw WebCrawlerException.invalidArguments("Uri cannot be null.");
        }

        if (uri.isOpaque()) {
            throw WebCrawlerException.invalidArguments(
                    "Invalid URI: '%s' must not be an opaque URI."
                            .formatted(uri)
            );
        }

        if (uri.getPath().startsWith("/")) {
            throw WebCrawlerException.invalidArguments(
                    "Invalid URI: '%s' must be url without a path e.g: valid: 'https://www.test.com', invalid: 'http://www.test.com/'"
                        .formatted(uri)
            );
        }

        if (!uri.getPath().isEmpty()) {
            throw WebCrawlerException.invalidArguments(
                    "Invalid URI: '%s' must be url without a path e.g: valid: 'https://www.test.com', invalid: 'http://www.test.com/test'"
                        .formatted(uri)
            );
        }
    }

}
