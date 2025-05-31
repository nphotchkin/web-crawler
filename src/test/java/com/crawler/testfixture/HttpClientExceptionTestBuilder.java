package com.crawler.testfixture;

import com.crawler.infrastructure.adapters.http.HttpClientException;
import com.crawler.infrastructure.adapters.http.RetryableHttpCodes;

import java.util.List;
import java.util.Map;

public class HttpClientExceptionTestBuilder {

    private int statusCode;
    private RetryableHttpCodes retryableStatus;
    private String responseBody;
    private Map<String, List<String>> responseHeaders;
    private String requestBody;
    private boolean isRetryable;
    private String reason;
    private Throwable throwable;

    private HttpClientExceptionTestBuilder() {}

    public static HttpClientExceptionTestBuilder aHttpClientException() {
        return new HttpClientExceptionTestBuilder();
    }

    public HttpClientExceptionTestBuilder ofRandomHttpError() {
        this.statusCode = 500;
        this.isRetryable = false;
        return this;
    }

    public HttpClientExceptionTestBuilder statusCode(int statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public HttpClientExceptionTestBuilder retryableStatus(RetryableHttpCodes retryableStatus) {
        this.retryableStatus = retryableStatus;
        return this;
    }

    public HttpClientExceptionTestBuilder responseBody(String responseBody) {
        this.responseBody = responseBody;
        return this;
    }

    public HttpClientExceptionTestBuilder responseHeaders(Map<String, List<String>> responseHeaders) {
        this.responseHeaders = responseHeaders;
        return this;
    }

    public HttpClientExceptionTestBuilder requestBody(String requestBody) {
        this.requestBody = requestBody;
        return this;
    }

    public HttpClientExceptionTestBuilder isRetryable(boolean isRetryable) {
        this.isRetryable = isRetryable;
        return this;
    }

    public HttpClientExceptionTestBuilder reason(String reason) {
        this.reason = reason;
        return this;
    }
    
    public HttpClientException build() {
        return new HttpClientException(
             reason,
             retryableStatus,
             statusCode,
             responseBody,
             responseHeaders,
             requestBody,
             isRetryable,
             throwable
        );
    }


}
