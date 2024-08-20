package com.ody.auth;

import com.ody.auth.token.AccessToken;
import com.ody.auth.token.RefreshToken;
import com.ody.member.domain.Member;
import io.jsonwebtoken.Jwts;
import java.time.LocalTime;
import java.util.Date;
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
        String memberId = Jwts.parser()
                .setSigningKey(authProperties.getAccessKey())
                .parseClaimsJws(accessToken.getValue())
                .getBody()
                .getSubject();
        return Long.parseLong(memberId);
    }

    public boolean isUnexpired(AccessToken accessToken) {
        System.out.println("accessToken = " + accessToken.getValue());
        Date expiration = Jwts.parser()
                .setSigningKey(authProperties.getAccessKey())
                .parseClaimsJws(accessToken.getValue())
                .getBody()
                .getExpiration();

        Date today = new Date();
        return today.before(expiration) || today.equals(expiration);
    }

    public boolean isUnexpired(RefreshToken refreshToken) {
        Date expiration = Jwts.parser()
                .setSigningKey(authProperties.getRefreshKey())
                .parseClaimsJws(refreshToken.getValue())
                .getBody()
                .getExpiration();

        Date today = new Date();
        return today.before(expiration) || today.equals(expiration);
    }
}
