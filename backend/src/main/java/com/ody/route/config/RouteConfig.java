package com.ody.route.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ody.route.service.GoogleRouteClient;
import com.ody.route.service.OdsayRouteClient;
import com.ody.route.service.RouteClient;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Profile("!test")
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(RouteClientProperties.class)
public class RouteConfig {

    private static final Duration DEFAULT_CONNECTION_TIMEOUT = Duration.ofSeconds(60);
    private static final Duration DEFAULT_READ_TIMEOUT = Duration.ofSeconds(30);

    private final RouteClientProperties properties;

    @Bean
    @Order(1)
    public RouteClient odysayRouteClient(ObjectMapper objectMapper) {
        RouteClientProperty property = properties.getProperty("odsay");
        return new OdsayRouteClient(property, builder(objectMapper));
    }

    @Bean
    @Order(2)
    public RouteClient googleRouteClient(ObjectMapper objectMapper) {
        RouteClientProperty property = properties.getProperty("google");
        return new GoogleRouteClient(property, builder(objectMapper));
    }

    @Bean
    public RestClient.Builder builder(ObjectMapper objectMapper) {
        return RestClient.builder()
                .requestFactory(new BufferingClientHttpRequestFactory(clientHttpRequestFactory()))
                .requestInterceptor(new RouteClientLoggingInterceptor(objectMapper));
    }

    private ClientHttpRequestFactory clientHttpRequestFactory() {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(DEFAULT_CONNECTION_TIMEOUT)
                .withReadTimeout(DEFAULT_READ_TIMEOUT);
        return ClientHttpRequestFactories.get(settings);
    }
}
