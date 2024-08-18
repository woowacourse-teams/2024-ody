package com.ody.auth.token;

import com.ody.auth.AuthProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import lombok.Getter;

@Getter
public class RefreshToken {

    private final String value;

    public RefreshToken(AuthProperties authProperties) {
        Date validity = new Date(System.currentTimeMillis() + authProperties.getRefreshExpiration());
        this.value = Jwts.builder()
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, authProperties.getRefreshKey())
                .compact();
    }
}
