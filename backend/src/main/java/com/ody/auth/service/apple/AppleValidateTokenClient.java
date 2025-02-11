package com.ody.auth.service.apple;

import com.ody.auth.config.AppleProperties;
import com.ody.auth.dto.response.AppleAuthValidationResponse;
import com.ody.common.exception.OdyServerErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Slf4j
@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(AppleProperties.class)
public class AppleValidateTokenClient {

    private final RestClient appleRestClient;
    private final AppleValidateTokenClientErrorHandler errorHandler;
    private final AppleClientSecretGenerator appleClientSecretGenerator;
    private final AppleProperties appleProperties;

    public String obtainRefreshToken(String authorizationCode) {
        String clientSecret = appleClientSecretGenerator.generate();
        AppleAuthValidationResponse appleAuthValidationResponse = validateAuthCode(clientSecret, authorizationCode);
        return appleAuthValidationResponse.refreshToken();
    }

    private AppleAuthValidationResponse validateAuthCode(String clientSecret, String authorizationCode) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", appleProperties.getClientId());
        body.add("client_secret", clientSecret);
        body.add("code", authorizationCode);
        body.add("grant_type", "authorization_code");

        try {
            return appleRestClient.post()
                    .uri(appleProperties.getValidateTokenUri())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(body)
                    .retrieve()
                    .onStatus(errorHandler)
                    .body(AppleAuthValidationResponse.class);
        } catch (RestClientException exception) {
            log.error(exception.getMessage());
            throw new OdyServerErrorException("Apple Authorization Code 검증에 실패했습니다: 담당자에게 문의하세요.");
        }
    }
}
