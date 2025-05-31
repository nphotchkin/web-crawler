package com.crawler.infrastructure.adapters.http.response;

import java.net.URI;
import java.util.Set;

public record HttpResponseLinkExtractionResult(
        Set<URI> validUris,
        Set<String> malformedPaths
) {}