package com.crawler.infrastructure.adapters.http;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Duration;

public class HttpClientBuilder {

    private static final Duration DEFAULT_CONNECT_TIMEOUT = Duration.ofSeconds(5);
    private static final Duration DEFAULT_REQUEST_TIMEOUT = Duration.ofSeconds(15);

    public HttpClientBuilder() {}

    public HttpClient.Builder createClientBuilder() {
        return HttpClient.newBuilder()
                .connectTimeout(DEFAULT_CONNECT_TIMEOUT);
    }

    public HttpRequest.Builder createRequestBuilder(URI uri) {
        return HttpRequest.newBuilder()
                .uri(uri)
                .timeout(DEFAULT_REQUEST_TIMEOUT);
    }

}
