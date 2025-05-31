package com.crawler.infrastructure.adapters.http;

import java.net.http.HttpResponse;
import java.util.Optional;

public interface BasicHttpClient {

    /**
     * Do get request parse body as string.
     * @param path - path part of a URL
     * @throws HttpClientException - A network or unexpected issue.
     */
    HttpResponse<String> getAsString(String path);

    /**
     * Do an options request to get the content type for the specified path.
     * @param path - path part of a URL
     * @throws HttpClientException - A network or unexpected issue.
     */
    Optional<String> getContentType(String path);

}
