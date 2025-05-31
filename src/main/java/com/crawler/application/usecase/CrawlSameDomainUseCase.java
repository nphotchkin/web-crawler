package com.crawler.application.usecase;

import com.crawler.infrastructure.adapters.crawler.report.CrawlerReport;

import java.net.URI;
import java.util.Set;

public interface CrawlSameDomainUseCase {

    CrawlerReport findAllUrlsForSameDomain(URI domain, Set<String> bannedPaths, int limit);

}
