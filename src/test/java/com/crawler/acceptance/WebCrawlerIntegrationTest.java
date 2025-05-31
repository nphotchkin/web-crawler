package com.crawler.acceptance;

import com.crawler.application.services.WebCrawlerService;
import com.crawler.infrastructure.adapters.crawler.WebCrawlerAdapter.WebCrawlerAdapterFactory;
import com.crawler.infrastructure.adapters.crawler.report.FailedCrawlEvent;
import com.crawler.testfixture.WebCrawlerAdapterTestFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.crawler.testfixture.HttpClientExceptionTestBuilder.aHttpClientException;
import static com.crawler.testfixture.MockHttpClientTestStub.whenDoGetRequestThenReturnPage200;
import static com.crawler.testfixture.WebpageMother.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebCrawlerIntegrationTest {

    @Mock
    private WebCrawlerAdapterFactory webCrawlerAdapterFactory;

    @InjectMocks
    private WebCrawlerService webScrapingService;

    private final URI uri = URI.create("https://www.monzo.com");
    private final String BANNED_PATH = "/banned/";

    @Test
    void findAllUrlsForSameDomain_should_crawlValidSiteMap_when_havingNoProblematicLinks() {
        // Given
        var bannedPaths = Set.of(BANNED_PATH);
        var adapterFactory = new WebCrawlerAdapterTestFactory();
        var mockHttpClient = adapterFactory.getHttpClient();
        var crawlerAdapter = adapterFactory.defaultConfig(uri, bannedPaths);
        when(webCrawlerAdapterFactory.defaultConfig(any(URI.class), anySet(), anyInt())).thenReturn(crawlerAdapter);

        when(mockHttpClient.getContentType(anyString())).thenReturn(Optional.of("text/html"));

        whenDoGetRequestThenReturnPage200(
                mockHttpClient, "", ValidSiteMapWithoutProblematicLinks.indexPage()
        );
        whenDoGetRequestThenReturnPage200(
                mockHttpClient, "/", ValidSiteMapWithoutProblematicLinks.indexPage()
        );
        whenDoGetRequestThenReturnPage200(
                mockHttpClient, "/valid-link/blog", ValidSiteMapWithoutProblematicLinks.pageLinkedToFromIndex()
        );

        // When
        var result = webScrapingService.findAllUrlsForSameDomain(uri, bannedPaths, 100);
        // Then
        var expectedExtractedUris = Set.of(
                URI.create("https://www.monzo.com/"),
                URI.create("https://www.monzo.com"),
                URI.create("https://www.monzo.com/valid-link/blog")
        );

        assertEquals(0, result.getFailedCrawlEvents().size());
        assertEquals(expectedExtractedUris, result.getValidExtractedUris());
    }

    @Test
    void findAllUrlsForSameDomain_shouldCrawlValidSiteMap_when_havingProblematicLinks() {
        // Given
        var bannedPaths = Set.of(BANNED_PATH);
        var adapterFactory = new WebCrawlerAdapterTestFactory();
        var mockHttpClient = adapterFactory.getHttpClient();
        var crawlerAdapter = adapterFactory.defaultConfig(uri, bannedPaths);
        when(webCrawlerAdapterFactory.defaultConfig(any(URI.class), anySet(), anyInt())).thenReturn(crawlerAdapter);

        when(mockHttpClient.getContentType(anyString())).thenReturn(Optional.of("text/html"));

        whenDoGetRequestThenReturnPage200(
                mockHttpClient, "", ValidSiteMapWithProblematicLinks.indexPage()
        );
        whenDoGetRequestThenReturnPage200(
                mockHttpClient, "/valid-link/blog", ValidSiteMapWithProblematicLinks.pageLinkedToFromIndex()
        );

        // When
        var result = webScrapingService.findAllUrlsForSameDomain(uri, bannedPaths, 100);
        // Then
        var expectedExtractedUris = Set.of(
                URI.create("https://www.monzo.com"),
                URI.create("https://www.monzo.com/valid-link/blog")
        );

        assertEquals(1, result.getFailedCrawlEvents().size());
        var expectedMalformedUris = List.of("/help/ monzo-pensions-transfers");
        var actualMalformedUris = result.getFailedCrawlEvents().stream()
                .flatMap(event -> event.getMalformedPathsOnPage().stream())
                .toList();

        assertThat(actualMalformedUris).isEqualTo(expectedMalformedUris);

        assertEquals(expectedExtractedUris, result.getValidExtractedUris());
    }

    @Test
    void findAllUrlsForSameDomain_shouldCrawlForBaseDomainOnly() {
        // Given
        var bannedPaths = Set.of(BANNED_PATH);
        var adapterFactory = new WebCrawlerAdapterTestFactory();
        var mockHttpClient = adapterFactory.getHttpClient();
        var crawlerAdapter = adapterFactory.defaultConfig(uri, bannedPaths);
        when(webCrawlerAdapterFactory.defaultConfig(any(URI.class), anySet(), anyInt())).thenReturn(crawlerAdapter);

        when(mockHttpClient.getContentType(anyString())).thenReturn(Optional.of("text/html"));

        whenDoGetRequestThenReturnPage200(
                mockHttpClient, "", ValidSiteMapWithSubDomainLinks.indexPage()
        );
        whenDoGetRequestThenReturnPage200(
                mockHttpClient, "/valid-link/blog", ValidSiteMapWithSubDomainLinks.pageLinkedToFromIndex()
        );
        // When
        var result = webScrapingService.findAllUrlsForSameDomain(uri, bannedPaths, 100);
        // Then - result doesnt include sub-domain links
        var expectedExtractedUris = Set.of(
                URI.create("https://www.monzo.com"),
                URI.create("https://www.monzo.com/valid-link/blog")
        );
        assertEquals(expectedExtractedUris, result.getValidExtractedUris());
    }

    @Test
    void findAllUrlsForSameDomain_shouldNotCrawlBannedPaths() {
        // Given
        var bannedPaths = Set.of(BANNED_PATH);
        var adapterFactory = new WebCrawlerAdapterTestFactory();
        var mockHttpClient = adapterFactory.getHttpClient();
        var crawlerAdapter = adapterFactory.defaultConfig(uri, bannedPaths);
        when(webCrawlerAdapterFactory.defaultConfig(any(URI.class), anySet(), anyInt())).thenReturn(crawlerAdapter);

        when(mockHttpClient.getContentType(anyString())).thenReturn(Optional.of("text/html"));

        whenDoGetRequestThenReturnPage200(
                mockHttpClient, "", SiteHavingBannedPaths.indexPage()
        );
        // When
        var result = webScrapingService.findAllUrlsForSameDomain(uri, bannedPaths, 100);
        // Then - result doesn't include any of 'banned' paths.
        var expectedExtractedUris = Set.of(URI.create("https://www.monzo.com"));
        assertEquals(expectedExtractedUris, result.getValidExtractedUris());
    }

    @Test
    void findAllUrlsForSameDomain_should_capturedFailedEvent_when_httpFetchFailed() {
        // Given
        var bannedPaths = Set.of(BANNED_PATH);
        var adapterFactory = new WebCrawlerAdapterTestFactory();
        var mockHttpClient = adapterFactory.getHttpClient();
        var crawlerAdapter = adapterFactory.defaultConfig(uri, bannedPaths);
        when(webCrawlerAdapterFactory.defaultConfig(any(URI.class), anySet(), anyInt())).thenReturn(crawlerAdapter);
        var exception = aHttpClientException().ofRandomHttpError().build();

        when(mockHttpClient.getContentType(anyString()))
                .thenThrow(exception);

        whenDoGetRequestThenReturnPage200(
                mockHttpClient, "", ValidSiteMapWithoutProblematicLinks.indexPage()
        );
        // When
        var result = webScrapingService.findAllUrlsForSameDomain(uri, bannedPaths, 100);
        // Then - result has failed URI.
        var expectedFailedCrawls = List.of("https://www.monzo.com");
        var actualFailedCrawls  = result.getFailedCrawlEvents().stream()
                .map(FailedCrawlEvent::getTargetUri)
                .toList();
        assertThat(actualFailedCrawls).isEqualTo(expectedFailedCrawls);
    }

}