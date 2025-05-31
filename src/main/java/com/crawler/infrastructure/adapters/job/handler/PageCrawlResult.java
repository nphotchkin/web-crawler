package com.crawler.infrastructure.adapters.job.handler;

import com.crawler.infrastructure.adapters.crawler.report.FailedCrawlEvent;

import java.net.URI;
import java.util.Set;

public record PageCrawlResult(
       URI targetUri, Set<FailedCrawlEvent> failedCrawlEvents, Set<URI> validExtractedUris
) {}