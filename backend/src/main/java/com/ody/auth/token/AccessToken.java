package com.ody.auth.token;

import com.ody.auth.AuthProperties;
import com.ody.member.domain.Member;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AccessToken {

    private final String value;

    public AccessToken(Member member, AuthProperties authProperties){
        Date validity = new Date(System.currentTimeMillis() + authProperties.getAccessExpiration());
        this.value =  Jwts.builder()
                .setSubject(member.getId().toString())
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, authProperties.getAccessKey())
                .compact();
    }
}
