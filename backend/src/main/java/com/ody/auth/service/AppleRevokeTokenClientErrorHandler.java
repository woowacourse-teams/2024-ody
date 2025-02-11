package com.ody.auth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ody.common.exception.OdyBadRequestException;
import com.ody.common.exception.OdyForbiddenException;
import com.ody.member.domain.AuthProvider;
import com.ody.member.service.MemberAppleTokenService;
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
public class AppleRevokeTokenClientErrorHandler implements ResponseErrorHandler {

    private AuthProvider authProvider;
    private final ObjectMapper objectMapper;
    private final MemberAppleTokenService memberAppleTokenService;

    public AppleRevokeTokenClientErrorHandler withAuthProvider(AuthProvider authProvider) {
        this.authProvider = authProvider;
        return this;
    }

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError();
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        Map responseBody = objectMapper.readValue(response.getBody(), Map.class);

        if (responseBody.get("error").equals("invalid_grant")) {
            memberAppleTokenService.delete(authProvider);
            throw new OdyForbiddenException("Apple Refresh Token이 유효하지 않습니다: 다시 로그인 후 시도해주세요.");
        }
        throw new OdyBadRequestException("Apple Token 철회에 실패했습니다: " + responseBody.get("error_description").toString());
    }
}
