package com.ody.auth.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "auth.access")
public class JwtAccessProperties {

    private final String key;
    private final long expiration;
}
