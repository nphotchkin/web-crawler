package com.crawler.testfixture;

import com.crawler.infrastructure.adapters.http.BasicHttpClient;

import java.net.http.HttpResponse;

import static org.mockito.Mockito.*;

public class MockHttpClientTestStub {

   public static void whenDoGetRequestThenReturnPage200(
            BasicHttpClient mockHttpClient,
            String path,
            String responseBody
    ) {
        var response = mock(HttpResponse.class);
        lenient().when(response.statusCode()).thenReturn(200);
        lenient().when(response.body()).thenReturn(responseBody);
        lenient().when(mockHttpClient.getAsString(path)).thenReturn(response);
    }

}
