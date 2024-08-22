package com.ody.auth.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class AuthServiceTest {

    @DisplayName("액세스 토큰을 파싱한다.")
    @Nested
    class parseAccessToken {

        @DisplayName("유효하고 만료되지 않은 액세스 토큰을 파싱할 수 있다.")
        @Test
        void parseAccessTokenSuccess() {

        }

        @DisplayName("유효하지 않거나 만료된 액세스 토큰을 파싱할 수 없다.")
        @ParameterizedTest
        @ValueSource(strings = "")
        void parseAccessTokenFail(String accessToken) {

        }
    }

    @DisplayName("액세스 토큰을 갱신한다.")
    @Nested
    class renewTokens {

        @DisplayName("만료된 액세스 토큰, 만료되지 않은 액세스 토큰이면 새 액세스 토큰, 리프레시 토큰을 반환한다.")
        @Test
        void renewTokensSuccess() {

        }

        @DisplayName("만료되지 않은 액세스 토큰이면 기존 액세스 토큰, 리프레시 토큰을 반환한다.")
        @Test
        void renewTokensWithUnexpiredAccessToken() {

        }

        @DisplayName("만료된 리프레시 토큰이면 401 에러가 발생한다.")
        @Test
        void renewTokensWithExpiredRefreshToken() {

        }
    }
}
