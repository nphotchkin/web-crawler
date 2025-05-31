package com.crawler.infrastructure.adapters.crawler.report;

import lombok.*;

import java.net.URI;
import java.util.Set;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class CrawlerReport {

    private Set<URI> validExtractedUris;
    private Set<FailedCrawlEvent> failedCrawlEvents;


    public String getPrettyReportString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n\n\n--- Crawler Report ---\n");
        sb.append("Valid Extracted URIs:\n");
        validExtractedUris.forEach(uri -> sb.append("  • ").append(uri).append("\n"));

        sb.append("Failed Crawl Events:\n");
        failedCrawlEvents.forEach(event -> sb.append("  • ").append(event).append("\n"));

        sb.append("-----------------------\n");
        return sb.toString();
    }
}