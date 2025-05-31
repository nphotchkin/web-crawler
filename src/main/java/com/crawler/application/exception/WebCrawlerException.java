package com.crawler.application.exception;

import com.crawler.infrastructure.adapters.http.HttpClientException;
import lombok.Getter;

@Getter
public class WebCrawlerException extends RuntimeException {

    private static final String SHOULD_NOT_HAPPEN_MESSAGE = "Oops! WebCrawler encountered an un-expected problem reason: '%s'";

    private final boolean doNotRetry;

    private WebCrawlerException(String message, boolean doNotRetry, Throwable cause) {
        super(message, cause);
        this.doNotRetry = doNotRetry;
    }
    private WebCrawlerException(String message, boolean doNotRetry) {
        super(message);
        this.doNotRetry = doNotRetry;
    }

    public static WebCrawlerException from(HttpClientException e) {
        var errorMessage = "Crawling failed crawling job details: '%s', reason: '%s'".formatted(e.details(), e.getMessage());
        return new WebCrawlerException(errorMessage, e.isRetryable(), e);
    }

    public static WebCrawlerException shouldNeverHappen(String reason, Exception e) {
        return new WebCrawlerException(SHOULD_NOT_HAPPEN_MESSAGE.formatted(reason), false, e);
    }

    public static WebCrawlerException shouldNeverHappen(String reason) {
        return new WebCrawlerException(SHOULD_NOT_HAPPEN_MESSAGE.formatted(reason), false);
    }

    public static WebCrawlerException invalidArguments(String reason) {
        return new WebCrawlerException("Invalid Argument: '%s'".formatted(reason), false);
    }

    public static WebCrawlerException unexpected(String reason) {
        return new WebCrawlerException("Unexpected: '%s'".formatted(reason), false);
    }

}
