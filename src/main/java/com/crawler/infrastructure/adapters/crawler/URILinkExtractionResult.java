package com.crawler.infrastructure.adapters.crawler;

import java.net.URI;
import java.util.Set;

public record URILinkExtractionResult(
        URI givenUri,
        Set<String> malformedLinks,
        Set<URI> validExtractedUris
) {}