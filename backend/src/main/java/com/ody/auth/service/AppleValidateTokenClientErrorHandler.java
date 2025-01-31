package com.ody.auth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ody.common.exception.OdyBadRequestException;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppleValidateTokenClientErrorHandler implements ResponseErrorHandler {

    private final ObjectMapper objectMapper;

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().is4xxClientError();
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        Map responseBody = objectMapper.readValue(response.getBody(), Map.class);
        throw new OdyBadRequestException("Apple Authorization Code 검증에 실패했습니다: " + responseBody.get("error_description").toString());
    }
}
