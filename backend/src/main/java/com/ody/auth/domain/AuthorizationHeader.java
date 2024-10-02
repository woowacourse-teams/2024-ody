package com.ody.auth.domain;

import com.ody.auth.token.AccessToken;
import com.ody.auth.token.RefreshToken;
import com.ody.common.exception.OdyBadRequestException;
import lombok.Getter;

@Getter
public class AuthorizationHeader {

    public static final String DELIMITER = " ";

    private final AccessToken accessToken;
    private final RefreshToken refreshToken;

    public AuthorizationHeader(String value) {
        String[] parsed = parse(value);
        validate(parsed);
        this.accessToken = new AccessToken(parsed[0] + DELIMITER + parsed[1]);
        this.refreshToken = new RefreshToken(parsed[2]);
    }

    private String[] parse(String value) {
        return value.split(DELIMITER);
    }

    private void validate(String[] parsed) {
        if (parsed.length != 3) {
            throw new OdyBadRequestException("토큰 방식, 액세스 토큰, 리프레시 토큰이 필요합니다.");
        }
    }
}
