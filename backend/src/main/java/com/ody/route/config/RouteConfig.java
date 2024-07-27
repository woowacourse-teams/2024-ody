package com.ody.route.config;

import com.ody.route.service.OdsayRouteClient;
import com.ody.route.service.RouteClient;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(RouteProperties.class)
@RequiredArgsConstructor
public class RouteConfig {

    private final RouteProperties routeProperties;

    @Bean
    @Profile("!test")
    public RouteClient routeClient(RestClient.Builder routeRestClientBuilder) {
//        routeRestClientCustomizer().customize(routeRestClientBuilder);
        return new OdsayRouteClient(routeProperties, routeRestClientBuilder);
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
