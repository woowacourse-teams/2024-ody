package com.ody.auth;

import com.ody.auth.token.AccessToken;
import com.ody.auth.token.RefreshToken;
import com.ody.member.domain.Member;
import io.jsonwebtoken.Jwts;
import java.util.Date;
import lombok.Getter;

@Getter
public class JwtTokenProvider {

    private final AuthProperties authProperties;

    public JwtTokenProvider(AuthProperties authProperties) {
        this.authProperties = authProperties;
    }

    public AccessToken createAccessToken(Member member) {
        return new AccessToken(member, authProperties);
    }

    public RefreshToken createRefreshToken() {
        return new RefreshToken(authProperties);
    }

    public String parseAccessToken(AccessToken token) {
        return Jwts.parser()
                .setSigningKey(authProperties.getAccessKey())
                .parseClaimsJws(token.getValue())
                .getBody()
                .getSubject();
    }

    public boolean validateAccessToken(AccessToken token) {
        Date expiration = Jwts.parser()
                .setSigningKey(authProperties.getAccessKey())
                .parseClaimsJws(token.getValue())
                .getBody()
                .getExpiration();

        Date today = new Date();
        return today.before(expiration) || today.equals(expiration);
    }
}
