package com.crawler.application.services;

import com.crawler.application.exception.WebCrawlerException;
import com.crawler.infrastructure.adapters.crawler.WebCrawlerAdapter;
import com.crawler.infrastructure.adapters.crawler.report.CrawlerReport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Stream;

import static com.crawler.infrastructure.adapters.crawler.WebCrawlerAdapter.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebCrawlerServiceTest {

    @Mock
    private WebCrawlerAdapterFactory webCrawlerAdapterFactory;

    @InjectMocks
    private WebCrawlerService webCrawlerService;

    @Test
    void findAllUrlsForSameDomain_shouldCrawlWebsite() {
        // Given
        var uri = URI.create("https://www.google.com");
        var bannedPaths = Set.of("/banned");
        var limit = 1;
        var mockWebCrawler = mock(WebCrawlerAdapter.class);
        var mockReport = mock(CrawlerReport.class);
        when(mockWebCrawler.crawlWebsite()).thenReturn(mockReport);
        when(webCrawlerAdapterFactory.defaultConfig(any(URI.class), anySet(), anyInt())).thenReturn(mockWebCrawler);
        // When
        var actualReport = webCrawlerService.findAllUrlsForSameDomain(uri, bannedPaths, limit);
        // Then - it should supply report from adapter
        verify(webCrawlerAdapterFactory).defaultConfig(uri, bannedPaths, limit);
        verify(mockWebCrawler).crawlWebsite();
        assertEquals(mockReport, actualReport);
    }

    @ParameterizedTest
    @MethodSource("invalidUriProvider")
    void findAllUrlsForSameDomain_should_throw_when_uri_is_invalid(URI uri) {
        assertThrows(WebCrawlerException.class, () ->
                webCrawlerService.findAllUrlsForSameDomain(uri, Collections.emptySet(), 1)
        );
    }

    static Stream<Arguments> invalidUriProvider() {
        return Stream.of(
        URI.create("https://example.com/"),
                URI.create("https://example.com/with-path"),
                URI.create("mailto:example@example.com")
        ).map(Arguments::of);
    }


}
