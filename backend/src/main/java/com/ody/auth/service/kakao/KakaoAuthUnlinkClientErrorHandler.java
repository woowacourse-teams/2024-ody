package com.ody.auth.service.kakao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ody.common.exception.OdyBadRequestException;
import com.ody.common.exception.OdyServerErrorException;
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
public class KakaoAuthUnlinkClientErrorHandler implements ResponseErrorHandler {

    private static final int NOT_REGISTERED_USER_CODE = -101;

    private final ObjectMapper objectMapper;

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError();
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        Map responseBody = objectMapper.readValue(response.getBody(), Map.class);

        log.info("카카오 연결 끊기를 실패했습니다. (응답: {})", responseBody);
        if (responseBody.get("code").equals(NOT_REGISTERED_USER_CODE)) {
            throw new OdyBadRequestException("카카오 연결 끊기 대상 유저가 아닙니다.");
        }
        throw new OdyServerErrorException("카카오 연결 끊기를 실패했습니다.");
    }
}
