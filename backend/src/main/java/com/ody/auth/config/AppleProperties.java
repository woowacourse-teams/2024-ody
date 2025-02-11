package com.ody.auth.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "apple")
public class AppleProperties {

    private final String privateKey;
    private final String keyId;
    private final String teamId;
    private final String clientId;
    private final String validateTokenUri;
    private final String revokeTokenUri;
    private final long clientSecretExpirationSeconds;
}
