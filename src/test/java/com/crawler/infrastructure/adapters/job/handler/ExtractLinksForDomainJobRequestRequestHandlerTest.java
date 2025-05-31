package com.crawler.infrastructure.adapters.job.handler;

import com.crawler.commons.JitterGenerator;
import com.crawler.infrastructure.adapters.crawler.CrawlerLinkExtractor;
import com.crawler.infrastructure.adapters.crawler.URILinkExtractionResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExtractLinksForDomainJobRequestRequestHandlerTest {

    @Mock
    private JitterGenerator jitterGenerator;

    @Mock
    private URI uri;

    @Mock
    private CrawlerLinkExtractor crawlerLinkExtractor;

    @Mock
    private Set<String> bannedPaths;

    @InjectMocks
    private ExtractLinksForDomainJobRequestRequestHandler handler;

    @BeforeEach
    void beforeEach() throws URISyntaxException {
        uri = new URI("https://example.com");
        bannedPaths = Set.of("/ignore");
        handler = new ExtractLinksForDomainJobRequestRequestHandler(jitterGenerator, crawlerLinkExtractor, uri, bannedPaths);
    }

    @Test
    void perform_should_extractLinksForTargetUri() throws URISyntaxException {
        // Given
        var linkExtractionResult = new URILinkExtractionResult(
                uri,
                Collections.emptySet(),
                Collections.emptySet()
        );
        when(crawlerLinkExtractor.extractLinksMatchingHostIgnoringBanned(any(), any(), any()))
                .thenReturn(linkExtractionResult);
        // When
        var result = handler.perform();
        // Then
        var expectedPageCrawlResult = new PageCrawlResult(
                new URI("https://example.com"),
                Collections.emptySet(),
                Collections.emptySet()
        );
        assertThat(result).isEqualTo(expectedPageCrawlResult);
    }

    @Test
    void perform_should_jitter() {
        // When
        handler.perform();
        // Then
        verify(jitterGenerator).waitAndJitter();
    }

}