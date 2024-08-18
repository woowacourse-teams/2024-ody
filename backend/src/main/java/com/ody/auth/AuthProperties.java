package com.ody.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "auth")
@RequiredArgsConstructor
public class AuthProperties {

    private final String accessKey;
    private final String refreshKey;
    private final long accessExpiration;
    private final long refreshExpiration;
}
