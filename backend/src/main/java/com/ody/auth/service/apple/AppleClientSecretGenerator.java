package com.ody.auth.service.apple;

import com.ody.auth.config.AppleProperties;
import com.ody.common.exception.OdyServerErrorException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(AppleProperties.class)
public class AppleClientSecretGenerator {

    private final AppleProperties appleProperties;

    public String generate() {
        try {
            Instant now = Instant.now();
            Date issuedAt = Date.from(now);
            Date expiresAt = Date.from(now.plusSeconds(appleProperties.getClientSecretExpirationSeconds()));

            return Jwts.builder()
                    .setHeaderParam("alg", "ES256")
                    .setHeaderParam("kid", appleProperties.getKeyId())
                    .setIssuer(appleProperties.getTeamId())
                    .setIssuedAt(issuedAt)
                    .setExpiration(expiresAt)
                    .setAudience("https://appleid.apple.com")
                    .setSubject(appleProperties.getClientId())
                    .signWith(SignatureAlgorithm.ES256, generatePrivateKey())
                    .compact();
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new OdyServerErrorException("Apple Client Secret 생성에 실패했습니다.");
        }
    }

    private PrivateKey generatePrivateKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = Base64.getDecoder().decode(appleProperties.getPrivateKey());
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        return keyFactory.generatePrivate(spec);
    }
}
