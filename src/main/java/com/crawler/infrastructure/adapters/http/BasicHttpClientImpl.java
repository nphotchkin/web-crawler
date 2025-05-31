package com.crawler.infrastructure.adapters.http;

import com.crawler.application.exception.WebCrawlerException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

@Slf4j
public class BasicHttpClientImpl implements BasicHttpClient {

    private final URI uri;
    private final HttpClient httpClient;
    private final HttpClientBuilder clientBuilder;

    public BasicHttpClientImpl(
            URI uri,
            HttpClientBuilder clientBuilder
    ) {
        this.uri = uri;
        this.clientBuilder = clientBuilder;
        this.httpClient = clientBuilder.createClientBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build();
    }

    public HttpResponse<String> getAsString(String path) {
        if (path == null) throw WebCrawlerException.invalidArguments("Path must not be null");

        var httpRequest = clientBuilder.createRequestBuilder(URI.create(uri + path))
                .GET()
                .build();

        return sendGetRequest(httpRequest);
    }

    public Optional<String> getContentType(String path) {
        if (path == null) throw WebCrawlerException.invalidArguments("Path must not be null");

        var requestUri = URI.create(uri + path);
        var headRequest = clientBuilder.createRequestBuilder(requestUri)
                .method("HEAD", HttpRequest.BodyPublishers.noBody())
                .build();

        try {
            log.trace("Sending OPTIONS request to '{}'", requestUri);
            var response = httpClient.send(headRequest, HttpResponse.BodyHandlers.discarding());
            if (response.statusCode() >= 400)
                throw HttpClientException.fromServerError(null, response.statusCode(),  response);

            var contentTypeHeaderValue = response.headers().firstValue("Content-Type");

            if (contentTypeHeaderValue.isEmpty()) {
                throw WebCrawlerException.unexpected(
                    "No Content-Type header found for path '%s'. Response headers: %s".formatted(
                            path, response.headers().map()
                    )
                );
            }
            return contentTypeHeaderValue;

        } catch (IOException | InterruptedException e) {
            throw HttpClientException.fromUnexpectedError(
                     null, e
            );
        }
    }

    private HttpResponse<String> sendGetRequest(HttpRequest request) {
        log.trace("Sending GET request to '{}'", uri);
        HttpResponse<String> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw HttpClientException.fromUnexpectedError(
                    null, e
            );
        }

        if (response.statusCode() >= 400)
            throw HttpClientException.fromServerError(null, response.statusCode(), response);

        return response;
    }

}
