package com.ody.auth.token;

import com.ody.auth.AuthProperties;
import com.ody.common.exception.OdyBadRequestException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import lombok.Getter;

@Getter
public class AccessToken implements JwtToken {

    private static final String ACCESS_TOKEN_PREFIX = "Bearer access-token=";

    private final String value;

    public AccessToken(long memberId, AuthProperties authProperties) {
        Date validity = new Date(System.currentTimeMillis() + authProperties.getAccessExpiration());
        this.value = Jwts.builder()
                .setSubject(String.valueOf(memberId))
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, authProperties.getAccessKey())
                .compact();
    }

    public AccessToken(String rawValue) {
        validate(rawValue);
        this.value = parseAccessToken(rawValue);
    }

    private void validate(String value) {
        if (!value.startsWith(ACCESS_TOKEN_PREFIX)) {
            throw new OdyBadRequestException("잘못된 액세스 토큰 형식입니다.");
        }
    }

    private String parseAccessToken(String rawValue) {
        return rawValue.substring(ACCESS_TOKEN_PREFIX.length()).trim();
    }

    @Override
    public String getSecretKey(AuthProperties authProperties) {
        return authProperties.getAccessKey();
    }
}
