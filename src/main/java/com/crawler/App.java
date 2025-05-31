package com.crawler;

import com.crawler.application.services.WebCrawlerService;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.util.Set;

import static com.crawler.infrastructure.adapters.crawler.WebCrawlerAdapter.*;

@Slf4j
public class App {

    static class Config {
        public static final Set<String> MONZO_BANNED_PATHS = Set.of(
                "/docs/",
                "/referral/",
                "/-staging-referral/",
                "/install/",
                "/blog/authors/",
                "/-deeplinks/"
        );

        public static final int limit = 100;
    }

    public static void main(String[] args) {
        var crawlerAdapterFactory = new WebCrawlerAdapterFactory();
        var crawlerService = new WebCrawlerService(crawlerAdapterFactory);
        var crawlerReport = crawlerService.findAllUrlsForSameDomain(
                URI.create("https://www.monzo.com"),
                Config.MONZO_BANNED_PATHS,
                Config.limit
        );
        log.info("Scraping result: {}", crawlerReport.getPrettyReportString());
        log.warn("Results were limited to: '%s', you must update the configuration if you'd like to crawl for longer"
                .formatted(Config.limit)
        );
    }

}
