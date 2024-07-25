package com.ody.route.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "odsay")
@RequiredArgsConstructor
@Getter
public class RouteProperties {

    private final String apiKey;
    private final String url;
}
