package com.crawler.infrastructure.adapters.crawler.report;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Set;

@Getter
@ToString
@EqualsAndHashCode
public class FailedCrawlEvent {

    private final String targetUri;
    private final FailedCrawlReason reason;
    private Set<String> malformedPathsOnPage;
    private String errorMessage;

    public FailedCrawlEvent(String targetUri, FailedCrawlReason reason, String errorMessage) {
        this.targetUri = targetUri;
        this.reason = reason;
        this.errorMessage = errorMessage;
    }

    public static FailedCrawlEvent ofHttpError(
            String failedCrawlUri, String errorMessage
    ) {
        return new FailedCrawlEvent(
             failedCrawlUri, FailedCrawlReason.HTTP_ERROR, errorMessage
        );
    }

    public FailedCrawlEvent(
            String targetUri,
            Set<String> malformedPathsOnPage
    ) {
        this.targetUri = targetUri;
        this.malformedPathsOnPage = malformedPathsOnPage;
        this.reason = FailedCrawlReason.MALFORMED;
    }

    public static FailedCrawlEvent ofMalformedUri(String targetUri, Set<String> malformedLinksOnPage) {
        return new FailedCrawlEvent(
                targetUri, malformedLinksOnPage
        );
    }

    public static FailedCrawlEvent ofUnexpectedException(String failedCrawlUri, Throwable throwable) {
        return new FailedCrawlEvent(failedCrawlUri, FailedCrawlReason.UNEXPECTED_EXCEPTION, throwable.getMessage());
    }

}
