package com.ody.auth.service;

import com.google.common.net.HttpHeaders;
import com.ody.auth.config.KakaoProperties;
import com.ody.common.exception.OdyBadRequestException;
import com.ody.member.domain.ProviderType;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
public class KakaoAuthUnlinkClient implements SocialAuthUnlinkClient {

    private final RestClient restClient;
    private final KakaoAuthUnlinkClientErrorHandler errorHandler;

    public KakaoAuthUnlinkClient(
            RestClient.Builder restClientBuilder,
            KakaoAuthUnlinkClientErrorHandler errorHandler,
            KakaoProperties kakaoProperties
    ) {
        this.restClient = restClientBuilder
                .baseUrl(kakaoProperties.getUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "KakaoAK " + kakaoProperties.getAdminKey())
                .build();
        this.errorHandler = errorHandler;
    }

    @Override
    public void unlink(String providerId) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("target_id_type", "user_id");
        body.add("target_id", providerId);

        try {
            Map responseBody = restClient.post()
                    .body(body)
                    .retrieve()
                    .onStatus(errorHandler)
                    .toEntity(Map.class).getBody();
            log.info("카카오 유저의 연결을 끊었습니다. (회원번호: {})", responseBody.get("id"));
        } catch (OdyBadRequestException exception) {

        }
    }

    @Override
    public ProviderType getProviderType() {
        return ProviderType.KAKAO;
    }
}
