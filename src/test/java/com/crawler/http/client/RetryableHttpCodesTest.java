package com.crawler.http.client;

import com.crawler.infrastructure.adapters.http.RetryableHttpCodes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static com.crawler.infrastructure.adapters.http.RetryableHttpCodes.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RetryableHttpCodesTest {

    private static final Stream<Integer> COMMON_SUCCESS_CODES = Stream.of(200, 201, 202, 204, 206);
    private static final Stream<Integer> RETRYABLE_CODES = Stream.of(429, 503, 504);

    @ParameterizedTest
    @MethodSource("isRetryable")
    void isRetryable_shouldBeRetryable_when_isRetryable(Integer status) {
        assertTrue(RetryableHttpCodes.fromCode(status).isRetryable());
    }

    @ParameterizedTest
    @MethodSource("isNotRetryable")
    void isRetryable_should_notBeRetryable_when_isNotRetryable(Integer status) {
        assertFalse(RetryableHttpCodes.fromCode(status).isRetryable());
    }

    @Test
    void notableHttpStatusCodes_should_fail_when_moreCodesAdded() {
        var declaredCodes = RetryableHttpCodes.values().length;
        var coveredCodes = List.of(TOO_MANY_REQUESTS, SERVICE_UNAVAILABLE, GATEWAY_TIMEOUT, NOT_RETRYABLE).size();

        if (declaredCodes > coveredCodes) fail(
                "One or more 'RetryableCodes' is not declared as being retryable or not, declared: '%s',  covered: '%s'"
                        .formatted(
                                declaredCodes, coveredCodes
                        )
        );
    }

    static Stream<Arguments> isRetryable() {
        return RETRYABLE_CODES.map(Arguments::of);
    }

    static Stream<Arguments> isNotRetryable() {
        return COMMON_SUCCESS_CODES.map(Arguments::of);
    }

}