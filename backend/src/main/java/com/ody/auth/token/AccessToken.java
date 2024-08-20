package com.ody.auth.token;

import com.ody.auth.AuthProperties;
import com.ody.common.exception.OdyBadRequestException;
import com.ody.common.exception.OdyUnauthorizedException;
import com.ody.member.domain.Member;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class AccessToken {

//    public static final String TOKEN_TYPE = "Bearer";
    private static final String ACCESS_TOKEN_PREFIX = "Bearer access-token=";
    public static final String DELIMITER = " ";

    private final String value;

    public AccessToken(String rawValue) {
//        String[] parsed = parse(rawValue);
        validate(rawValue);
        this.value = parseAccessToken(rawValue);
    }

    private String[] parse(String value) {
        return value.split(DELIMITER);
    }

    private void validate(String value) {
//        if (parsed.length != 2) {
//            throw new OdyUnauthorizedException("토큰 방식, 액세스 토큰이 필요합니다.");
//        }

//        if (!parsed[0].equals(TOKEN_TYPE)) {
//            throw new OdyBadRequestException("잘못된 인증 방식입니다.");
//        }
        if (!value.startsWith(ACCESS_TOKEN_PREFIX)) {
            throw new OdyBadRequestException("잘못된 액세스 토큰 형식입니다.");
        }
    }

    private String parseAccessToken(String rawValue) {
        return rawValue.substring(ACCESS_TOKEN_PREFIX.length()).trim();
    }

    public AccessToken(long memberId, AuthProperties authProperties) {
        Date validity = new Date(System.currentTimeMillis() + authProperties.getAccessExpiration());
        this.value = Jwts.builder()
                .setSubject(String.valueOf(memberId))
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, authProperties.getAccessKey())
                .compact();
    }
}
