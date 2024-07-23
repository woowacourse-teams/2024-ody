package com.ody.member.domain;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ody.common.exception.OdyException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DeviceTokenTest {

    @DisplayName("디바이스 토큰 헤더는 접두사가 포함되어야 한다")
    @Test
    void createDeviceTokenSuccess() {
        assertThatCode(() -> new DeviceToken("device-token=1234"))
                .doesNotThrowAnyException();
    }

    @DisplayName("디바이스 토큰 헤더는 접두사가 포함되지 않으면 예외가 발생한다")
    @Test
    void createDeviceTokenException() {
        assertThatThrownBy(() -> new DeviceToken("1234"))
                .isInstanceOf(OdyException.class);
    }
}
