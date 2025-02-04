package com.ody.auth.config;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
@RequiredArgsConstructor
public class AppleConfig {

    private static final String BASE_URL = "https://appleid.apple.com";
    private static final Duration DEFAULT_CONNECTION_TIMEOUT = Duration.ofSeconds(5);
    private static final Duration DEFAULT_READ_TIMEOUT = Duration.ofSeconds(5);

    @Bean
    public RestClient appleRestClient() {
        return RestClient.builder()
                .baseUrl(BASE_URL)
                .requestFactory(new BufferingClientHttpRequestFactory(clientHttpRequestFactory()))
                .build();
    }

    private ClientHttpRequestFactory clientHttpRequestFactory() {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(DEFAULT_CONNECTION_TIMEOUT)
                .withReadTimeout(DEFAULT_READ_TIMEOUT);
        return ClientHttpRequestFactories.get(settings);
    }
}
