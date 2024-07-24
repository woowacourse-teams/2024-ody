package com.ody.member.domain;

import com.ody.common.exception.OdyException;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeviceToken {

    private static final String DEVICE_TOKEN_PREFIX = "Bearer device-token=";

    private String deviceToken;

    public DeviceToken(String deviceToken) {
        validatePrefix(deviceToken);
        this.deviceToken = deviceToken.substring(DEVICE_TOKEN_PREFIX.length());
    }

    private void validatePrefix(String value) {
        if (!value.startsWith(DEVICE_TOKEN_PREFIX)) {
            throw new OdyException("잘못된 토큰 형식입니다.");
        }
    }
}
