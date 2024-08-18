package com.ody.member.domain;

import com.ody.common.exception.OdyUnauthorizedException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeviceToken {

    private static final String DEVICE_TOKEN_PREFIX = "Bearer device-token=";

    @Column(name = "deviceToken", unique = true, nullable = false)
    private String value;

    public DeviceToken(String value) {
        validatePrefix(value);
        String token = value.substring(DEVICE_TOKEN_PREFIX.length())
                .strip();
        validateBlank(token);
        this.value = token;
    }

    private void validatePrefix(String value) {
        if (!value.startsWith(DEVICE_TOKEN_PREFIX)) {
            throw new OdyUnauthorizedException("잘못된 토큰 형식입니다.");
        }
    }

    private void validateBlank(String token) {
        if (token.isBlank()) {
            throw new OdyUnauthorizedException("토큰 값은 공백이 될 수 없습니다.");
        }
    }
}
