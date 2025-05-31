package com.crawler.http.client;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.crawler.infrastructure.adapters.http.BasicHttpClient;
import com.crawler.infrastructure.adapters.http.BasicHttpClientImpl;
import com.crawler.infrastructure.adapters.http.HttpClientBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.net.URI;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(WireMockExtension.class)
class BasicHttpClientImplTest {

    @RegisterExtension
    private final static WireMockExtension wiremock = WireMockExtension.newInstance()
            .options(WireMockConfiguration.options().dynamicPort())
            .build();

    private static final String BASE_URL = "http://localhost";
    private static final String PATH = "/path";
    private BasicHttpClient client;

    @BeforeEach
    void beforeEach() {
        client = new BasicHttpClientImpl(URI.create(BASE_URL + ":" + wiremock.getPort()), new HttpClientBuilder());
    }

    @Test
    void getAsString_should_doGetRequest_when_executeWithValidBaseUriAndPath() {
        // Given
        var body = "<response>SUCCESS</response>";
        stubForValidGetRequestHaving(body);
        // When
        var response = client.getAsString(PATH);
        // Then
        assertEquals(200, response.statusCode());
        assertEquals(body, response.body());
        assertEquals("text/xml", response.headers().firstValue("Content-Type").orElse(null));
    }

    @Test
    void getContentType_should_sendHeadRequestAndSupplyContentType_when_executeWithValidBaseUriAndPath() {
        // Given
        stubForValidOptionsRequest();
        // When
        var contentType = client.getContentType(PATH);
        // Then
        if (contentType.isEmpty()) fail("Content type must not be empty");
        assertEquals("text/xml", contentType.get());
    }

    private void stubForValidGetRequestHaving(String responseBody) {
        wiremock.stubFor(get(urlEqualTo(PATH))
                .willReturn(
                        ok()
                                .withHeader("Content-Type", "text/xml")
                                .withBody(responseBody)
                )
        );
    }

    private void stubForValidOptionsRequest() {
        wiremock.stubFor(head(urlEqualTo(PATH))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")));
    }

}