package com.ody.auth.token;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ody.common.exception.OdyBadRequestException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RefreshTokenTest {

    @DisplayName("리프레시 토큰이 널이면 400 에러가 발생한다.")
    @Test
    void nullRefreshTokenException() {
        assertThatThrownBy(() -> new RefreshToken((String) null))
                .isInstanceOf(OdyBadRequestException.class);
    }
}
