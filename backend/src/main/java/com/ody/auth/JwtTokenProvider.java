package com.ody.auth;

import com.ody.auth.token.AccessToken;
import com.ody.auth.token.RefreshToken;
import com.ody.common.exception.OdyBadRequestException;
import com.ody.common.exception.OdyUnauthorizedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.Getter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Component
@EnableConfigurationProperties(AuthProperties.class)
public class JwtTokenProvider {

    private final AuthProperties authProperties;

    public JwtTokenProvider(AuthProperties authProperties) {
        this.authProperties = authProperties;
    }

    public AccessToken createAccessToken(long memberId) {
        return new AccessToken(memberId, authProperties);
    }

    public RefreshToken createRefreshToken() {
        return new RefreshToken(authProperties);
    }

    public long parseAccessToken(AccessToken accessToken) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(authProperties.getAccessKey())
                    .parseClaimsJws(accessToken.getValue())
                    .getBody();
            return Long.parseLong(claims.getSubject());
        } catch (ExpiredJwtException exception) {
            return Long.parseLong(exception.getClaims().getSubject());
        } catch (JwtException exception) {
            throw new OdyBadRequestException(exception.getMessage());
        }
    }

    public void validate(AccessToken accessToken) {
        if (!isUnexpired(accessToken)) {
            throw new OdyUnauthorizedException("만료된 액세스 토큰입니다.");
        }
    }

    public void validate(RefreshToken refreshToken) {
        if (!isUnexpired(refreshToken)) {
            throw new OdyUnauthorizedException("만료된 리프레시 토큰입니다.");
        }
    }

    public boolean isUnexpired(AccessToken accessToken) {
        try {
            Jwts.parser()
                    .setSigningKey(authProperties.getAccessKey())
                    .parseClaimsJws(accessToken.getValue());
            return true;
        } catch (ExpiredJwtException exception) {
            return false;
        } catch (JwtException exception) {
            throw new OdyBadRequestException(exception.getMessage());
        }
    }

    public boolean isUnexpired(RefreshToken refreshToken) {
        try {
            Jwts.parser()
                    .setSigningKey(authProperties.getRefreshKey())
                    .parseClaimsJws(refreshToken.getValue());
            return true;
        } catch (ExpiredJwtException exception) {
            return false;
        } catch (JwtException exception) {
            throw new OdyBadRequestException(exception.getMessage());
        }
    }
}
