package com.crawler.infrastructure.adapters.http;

import com.crawler.commons.EnumUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

import static com.crawler.infrastructure.adapters.http.RetryableHttpCodes.NOT_RETRYABLE;

@ToString
@EqualsAndHashCode(callSuper = false)
public class HttpClientException extends RuntimeException {

    private final int statusCode;
    private final RetryableHttpCodes retryableStatus;
    private final String responseBody;
    private final Map<String, List<String>> responseHeaders;
    private final String requestBody;

    @Getter
    private final boolean isRetryable;
    private final String message;

    public HttpClientException(
            String message,
            RetryableHttpCodes retryableStatus,
            int statusCode,
            String responseBody,
            Map<String, List<String>> responseHeaders,
            String requestBody,
            boolean isRetryable,
            Throwable cause
    ) {
        super(message, cause);
        this.retryableStatus = retryableStatus;
        this.statusCode = statusCode;
        this.responseBody = responseBody;
        this.responseHeaders = responseHeaders;
        this.requestBody = requestBody;
        this.isRetryable = isRetryable;
        this.message = message;
    }

    public static HttpClientException fromUnexpectedError(String requestBody, Exception cause) {
        return new HttpClientException(
                "Network error during HTTP request",
                NOT_RETRYABLE,
                -1,
                null,
                null,
                requestBody,
                true,
                cause
        );
    }

    public static HttpClientException fromServerError(String requestBody, int statusCode, HttpResponse<?> response) {
        var retryableStatus = EnumUtils.getOrDefault(RetryableHttpCodes.class, String.valueOf(response.statusCode()), NOT_RETRYABLE);
        return new HttpClientException(
                "HTTP error response",
                retryableStatus,
                statusCode,
                (String) response.body(),
                response.headers().map(),
                requestBody,
                retryableStatus.isRetryable(),
                null
        );
    }

    public String details() {
        return String.format(
                "retryableStatus: '%s', responseBody: '%s', statusCode: '%s', responseHeaders: '%s', requestBody: '%s', retryable: '%s'",
                retryableStatus, responseBody, statusCode, responseHeaders, requestBody, isRetryable
        );
    }

}
