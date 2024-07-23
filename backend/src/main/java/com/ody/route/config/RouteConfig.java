package com.ody.route.config;

import com.ody.route.service.RouteClient;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(RouteProperties.class)
@RequiredArgsConstructor
public class RouteConfig {

    private final RouteProperties routeProperties;

    @Bean
    public RouteClient routeClient(RestClient.Builder routeRestClientBuilder) {
        return new RouteClient(routeProperties, routeRestClientBuilder);
    }

    @Bean
    public RestClientCustomizer routeRestClientCustomizer() {
        return builder -> builder.requestFactory(clientHttpRequestFactory())
                .build();
    }

    private ClientHttpRequestFactory clientHttpRequestFactory() {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofSeconds(10))
                .withReadTimeout(Duration.ofSeconds(30));
        return ClientHttpRequestFactories.get(settings);
    }
}
