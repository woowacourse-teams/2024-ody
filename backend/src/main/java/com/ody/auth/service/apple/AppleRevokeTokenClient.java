package com.ody.auth.service.apple;

import com.ody.auth.config.AppleProperties;
import com.ody.auth.service.SocialAuthUnlinkClient;
import com.ody.common.exception.OdyServerErrorException;
import com.ody.member.domain.AuthProvider;
import com.ody.member.domain.ProviderType;
import com.ody.member.service.MemberAppleTokenService;
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
public class AppleRevokeTokenClient implements SocialAuthUnlinkClient {

    private final RestClient appleRestClient;
    private final AppleProperties appleProperties;
    private final AppleRevokeTokenClientErrorHandler errorHandler;
    private final MemberAppleTokenService memberAppleTokenService;
    private final AppleClientSecretGenerator appleClientSecretGenerator;

    @Override
    public void unlink(String providerId) {
        String clientSecret = appleClientSecretGenerator.generate();

        AuthProvider authProvider = new AuthProvider(ProviderType.APPLE, providerId);
        String appleRefreshToken = memberAppleTokenService.findAppleRefreshToken(authProvider);

        revokeToken(clientSecret, authProvider, appleRefreshToken);
    }

    private void revokeToken(String clientSecret, AuthProvider authProvider, String appleRefreshToken) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", appleProperties.getClientId());
        body.add("client_secret", clientSecret);
        body.add("token", appleRefreshToken);
        body.add("token_type_hint", "refresh_token");

        try {
            appleRestClient.post()
                    .uri(appleProperties.getRevokeTokenUri())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(body)
                    .retrieve()
                    .onStatus(errorHandler.withAuthProvider(authProvider))
                    .toBodilessEntity();
        } catch (RestClientException exception) {
            log.error(exception.getMessage());
            throw new OdyServerErrorException("Apple Token 철회에 실패했습니다: 담당자에게 문의하세요.");
        }
    }

    @Override
    public ProviderType getProviderType() {
        return ProviderType.APPLE;
    }
}
