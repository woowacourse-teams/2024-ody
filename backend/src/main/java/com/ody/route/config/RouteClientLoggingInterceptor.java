package com.ody.route.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

@Slf4j
@RequiredArgsConstructor
public class RouteClientLoggingInterceptor implements ClientHttpRequestInterceptor {

    private final ObjectMapper objectMapper;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        log.info("[RouteClient Request] Method: {}, URI: {}", request.getMethod(), request.getURI());

        ClientHttpResponse response = execution.execute(request, body);

        String responseBody = StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8);
        String singleLineBody = convertToSingleLine(responseBody);
        log.info("[RouteClient Response] Status: {}, Body: {}", response.getStatusCode(), singleLineBody);
        return response;
    }

    private String convertToSingleLine(String jsonString) throws IOException {
        Object json = objectMapper.readValue(jsonString, Object.class);
        return objectMapper.writeValueAsString(json);
    }
}
