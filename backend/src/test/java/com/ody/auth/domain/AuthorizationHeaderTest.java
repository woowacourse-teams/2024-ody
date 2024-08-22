package com.ody.auth.domain;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ody.common.exception.OdyBadRequestException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class AuthorizationHeaderTest {

    @DisplayName("유효한 AuthorizationHeader를 생성한다.")
    @Test
    void createAuthorizationHeaderSuccess() {
        assertThatCode(() -> new AuthorizationHeader("Bearer access-token=accessToken refresh-token=refreshToken"))
                .doesNotThrowAnyException();
    }

    @DisplayName("유효하지 않은 AuthorizationHeader를 생성하면 400 예외가 발생한다.")
    @ParameterizedTest
    @ValueSource(strings = {
            "Bearer access-token=accessToken",
            "access-token=accessToken refresh-token=refreshToken",
            "Bearer access-token=accessToken&refresh-token=refreshToken",
            "Bearer refresh-token=refreshToken"
    })
    void createAuthorizationHeaderException(String rawAuthorizationHeader) {
        assertThatThrownBy(() -> new AuthorizationHeader(rawAuthorizationHeader))
                .isInstanceOf(OdyBadRequestException.class);
    }
}
