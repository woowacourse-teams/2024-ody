package com.ody.auth.config;

import com.ody.auth.service.kakao.KakaoAuthUnlinkClient;
import com.ody.auth.service.kakao.KakaoAuthUnlinkClientErrorHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(KakaoProperties.class)
public class KakaoConfig {

    private final KakaoProperties kakaoProperties;

    @Bean
    public KakaoAuthUnlinkClient kakaoAuthUnlinkClient(
            RestClient.Builder routeRestClientBuilder,
            KakaoAuthUnlinkClientErrorHandler errorHandler,
            KakaoProperties kakaoProperties
    ) {
        return new KakaoAuthUnlinkClient(routeRestClientBuilder, errorHandler, kakaoProperties);
    }
}
