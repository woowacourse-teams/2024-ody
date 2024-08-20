package com.ody.auth.token;

import com.ody.auth.AuthProperties;
import com.ody.common.exception.OdyBadRequestException;
import com.ody.common.exception.OdyUnauthorizedException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Date;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    public static final String REFRESH_TOKEN_PREFIX = "refresh-token=";

    @Column(name = "refreshToken")
    private String value;

    public RefreshToken(String rawValue) {
        validate(rawValue);
        this.value = parseRefreshToken(rawValue);
    }

    private void validate(String value) {
        if (!value.startsWith(REFRESH_TOKEN_PREFIX)) {
            throw new OdyBadRequestException("잘못된 리프레시 토큰 형식입니다.");
        }
    }

    private String parseRefreshToken(String rawValue) {
        return rawValue.substring(REFRESH_TOKEN_PREFIX.length()).trim();
    }

    public RefreshToken(AuthProperties authProperties) {
        Date validity = new Date(System.currentTimeMillis() + authProperties.getRefreshExpiration());
        this.value = Jwts.builder()
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, authProperties.getRefreshKey())
                .compact();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RefreshToken that = (RefreshToken) o;
        return Objects.equals(getValue(), that.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue());
    }
}
