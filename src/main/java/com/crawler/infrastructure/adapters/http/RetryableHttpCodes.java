package com.crawler.infrastructure.adapters.http;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum RetryableHttpCodes {

    TOO_MANY_REQUESTS(429, true),
    SERVICE_UNAVAILABLE(503, true),
    GATEWAY_TIMEOUT(504, true),
    NOT_RETRYABLE(-1, false);  // Fallback enum constant

    private final int code;
    private final boolean isRetryable;

    RetryableHttpCodes(int code, boolean isRetryable) {
        this.code = code;
        this.isRetryable = isRetryable;
    }

    public boolean isRetryable() {
        return isRetryable;
    }

    public static RetryableHttpCodes fromCode(int code) {
        return Arrays.stream(RetryableHttpCodes.values())
                .filter(it -> it.getCode() == code)
                .findFirst()
                .orElse(NOT_RETRYABLE);
    }

}
