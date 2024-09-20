package com.ody.route.config;

import com.ody.route.service.GoogleRouteClient;
import com.ody.route.service.OdsayRouteClient;
import com.ody.route.service.RouteClient;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(RouteProperties.class)
@RequiredArgsConstructor
public class RouteConfig {

    private final RouteProperties routeProperties;
    private String googleApiKey;

    @Bean
    @Order(1)
    @Profile("!test")
    public RouteClient odySayRouteClient(RestClient.Builder routeRestClientBuilder) {
        return new OdsayRouteClient(routeProperties, routeRestClientBuilder);
    }

    @Bean
    @Order(2)
    @Profile("!test")
    public RouteClient GoogleRouteClient(
            RestClient.Builder routeRestClientBuilder,
            @Value("${google.maps.api-key}") String googleApiKey
    ) {
        return new GoogleRouteClient(routeRestClientBuilder, googleApiKey);
    }

    @Bean
    public RestClientCustomizer routeRestClientCustomizer() {
        return builder -> builder.requestFactory(clientHttpRequestFactory())
                .build();
    }

    private ClientHttpRequestFactory clientHttpRequestFactory() {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofSeconds(60))
                .withReadTimeout(Duration.ofSeconds(30)); //TODO: timeout 처리 로직 구현
        return ClientHttpRequestFactories.get(settings);
    }
}
