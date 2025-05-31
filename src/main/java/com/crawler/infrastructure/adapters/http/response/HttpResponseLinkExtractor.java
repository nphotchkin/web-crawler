package com.crawler.infrastructure.adapters.http.response;

import java.net.URI;
import java.net.http.HttpResponse;
import java.util.Set;

public interface HttpResponseLinkExtractor {

    /**
     * Extract the links for the specified domain, any subdomains are ignored
     * on no matches returns an empty set. Silently ignores any malformed links or hosts.
     *
     * @param baseUri - URI the response was extracted for.
     * @param response - Response body having links to extract.
     * @return - Distinct links on this page.
     */
    HttpResponseLinkExtractionResult extractLinksHavingSameDomain(
            URI baseUri, HttpResponse<String> response, Set<String> ignorePathsInResponse
    );

}
