package com.crawler.testfixture;


import com.crawler.commons.JitterGenerator;
import com.crawler.infrastructure.adapters.crawler.CrawlerLinkExtractor;
import com.crawler.infrastructure.adapters.crawler.WebCrawlerAdapter;
import com.crawler.infrastructure.adapters.http.BasicHttpClient;
import com.crawler.infrastructure.adapters.http.response.HttpResponseLinkExtractorImpl;
import com.crawler.infrastructure.adapters.job.BasicFixedSizedThreadPoolProvider;
import com.crawler.infrastructure.adapters.job.JobExecutor;

import java.net.URI;
import java.util.Set;

import static org.mockito.Mockito.mock;

public class WebCrawlerAdapterTestFactory {

    private final BasicHttpClient mockHttpClient;

    public WebCrawlerAdapterTestFactory() {
        this.mockHttpClient = mock(BasicHttpClient.class);
    }

    public WebCrawlerAdapter defaultConfig(URI baseDomain, Set<String> bannedPaths) {
        var linkExtractor = new CrawlerLinkExtractor(
                mockHttpClient,
                new HttpResponseLinkExtractorImpl()
        );
        return new WebCrawlerAdapter(
                baseDomain,
                bannedPaths,
                linkExtractor,
                10000,
                new JobExecutor(BasicFixedSizedThreadPoolProvider.withDefaultConfiguration()),
                mock(JitterGenerator.class)
        );
    }

    public BasicHttpClient getHttpClient() {
        return mockHttpClient;
    }
}