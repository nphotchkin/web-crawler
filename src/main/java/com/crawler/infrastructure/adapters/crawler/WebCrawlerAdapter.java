package com.crawler.infrastructure.adapters.crawler;

import com.crawler.commons.JitterGenerator;
import com.crawler.infrastructure.adapters.http.BasicHttpClientImpl;
import com.crawler.infrastructure.adapters.http.HttpClientBuilder;
import com.crawler.infrastructure.adapters.http.response.HttpResponseLinkExtractorImpl;
import com.crawler.infrastructure.adapters.job.BasicFixedSizedThreadPoolProvider;
import com.crawler.infrastructure.adapters.job.JobExecutor;
import com.crawler.infrastructure.adapters.job.handler.ExtractLinksForDomainJobRequestRequestHandler;
import com.crawler.infrastructure.adapters.crawler.report.CrawlerReport;
import com.crawler.infrastructure.adapters.crawler.report.FailedCrawlEvent;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class WebCrawlerAdapter {

    private final URI baseDomain;
    private final Set<String> bannedPaths;
    private final int limit;
    private final CrawlerLinkExtractor crawlerLinkExtractor;
    private final JobExecutor jobExecutor;
    private final JitterGenerator jitterGenerator;

    private final AtomicInteger crawlCounter = new AtomicInteger();
    private final Set<URI> attemptedToVisitUrls = ConcurrentHashMap.newKeySet();
    private final Set<URI> validExtractedUris = ConcurrentHashMap.newKeySet();
    private final Set<FailedCrawlEvent> failedCrawlEvents = ConcurrentHashMap.newKeySet();
    private final AtomicInteger activeJobCount = new AtomicInteger(0);
    private final CompletableFuture<Void> crawlingDone = new CompletableFuture<>();

    public WebCrawlerAdapter(
            URI baseDomain,
            Set<String> bannedPaths,
            CrawlerLinkExtractor crawlerLinkExtractor,
            int limit,
            JobExecutor jobExecutor,
            JitterGenerator jitterGenerator
    ) {
        this.baseDomain = baseDomain;
        this.crawlerLinkExtractor = crawlerLinkExtractor;
        this.bannedPaths = bannedPaths;
        this.limit = limit;
        this.jobExecutor = jobExecutor;
        this.jitterGenerator = jitterGenerator;
    }

    public static class WebCrawlerAdapterFactory {
        public WebCrawlerAdapter defaultConfig(URI baseDomain, Set<String> bannedPaths, int limit) {
            var linkExtractor = new CrawlerLinkExtractor(
                    new BasicHttpClientImpl(baseDomain, new HttpClientBuilder()),
                    new HttpResponseLinkExtractorImpl()
            );
            return new WebCrawlerAdapter(
                    baseDomain,
                    bannedPaths,
                    linkExtractor,
                    limit,
                    new JobExecutor(BasicFixedSizedThreadPoolProvider.withDefaultConfiguration()),
                    new JitterGenerator()
            );
        }
    }

    public CrawlerReport crawlWebsite() {
        activeJobCount.addAndGet(1);
        crawl(baseDomain);
        crawlingDone.join(); // block until all jobs complete
        return new CrawlerReport(validExtractedUris, failedCrawlEvents);
    }

    private void crawl(URI uri) {
        if (!isCrawlable(uri)) {
            completeIfNoActiveJobs();
            return;
        }
        attemptedToVisitUrls.add(uri);
        if (crawlCounter.incrementAndGet() > limit) {
            completeIfNoActiveJobs();
            return;
        }
        queueNextCrawlAwaitResultsAsync(uri);
    }

    private void completeIfNoActiveJobs() {
        if (activeJobCount.decrementAndGet() == 0) {
            crawlingDone.complete(null);
        }
    }

    private boolean isCrawlable(URI uri) {

        return isNotBanned(uri) && isBaseDomainHostAndNotYetVisited(uri);
    }

    public boolean isNotBanned(URI uri) {
        String path = uri.getPath();
        return bannedPaths.stream().noneMatch(path::startsWith);
    }

    private boolean isBaseDomainHostAndNotYetVisited(URI uri) {
        return uri.getHost().equals(baseDomain.getHost()) && !attemptedToVisitUrls.contains(uri);
    }

    private void queueNextCrawlAwaitResultsAsync(URI uri) {
        log.info("Crawling: '{}'", uri);
        var job = jobExecutor.execute(
                new ExtractLinksForDomainJobRequestRequestHandler(
                        jitterGenerator, crawlerLinkExtractor, uri, bannedPaths
                )
        );
        job.getResultFuture()
                .thenAccept(result -> {
                    failedCrawlEvents.addAll(result.failedCrawlEvents());
                    validExtractedUris.add(result.targetUri());

                    result.validExtractedUris().stream()
                            .filter(it -> crawlCounter.get() < limit) // don't queue more if limit reached
                            .filter(this::isCrawlable)
                            .forEach(foundUri -> {
                                activeJobCount.incrementAndGet();
                                crawl(foundUri);
                            });
                    completeIfNoActiveJobs();
                });

    }

}
