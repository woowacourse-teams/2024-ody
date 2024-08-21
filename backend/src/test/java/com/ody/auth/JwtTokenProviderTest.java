package com.ody.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ody.auth.token.AccessToken;
import com.ody.auth.token.RefreshToken;
import com.ody.common.BaseServiceTest;
import com.ody.common.exception.OdyBadRequestException;
import com.ody.common.exception.OdyException;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

class JwtTokenProviderTest extends BaseServiceTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private long memberId;
    private AuthProperties authProperties;
    private AuthProperties authPropertiesForExpiredToken;
    private AuthProperties authPropertiesForInvalidToken;
    private AccessToken validAccessToken;
    private AccessToken expiredAccessToken;
    private AccessToken invalidAccessToken;
    private RefreshToken validRefreshToken;
    private RefreshToken expiredRefreshToken;
    private RefreshToken invalidRefreshToken;

    @BeforeEach
    void setUp() {
        memberId = 1L;

        authProperties = jwtTokenProvider.getAuthProperties();
        authPropertiesForExpiredToken = new AuthProperties(
                authProperties.getAccessKey(),
                authProperties.getRefreshKey(),
                0,
                0
        );
        authPropertiesForInvalidToken = new AuthProperties(
                "wrongAccessKey",
                "wrongRefreshKey",
                60000,
                60000
        );

        validAccessToken = jwtTokenProvider.createAccessToken(memberId);
        expiredAccessToken = new AccessToken(memberId, authPropertiesForExpiredToken);
        invalidAccessToken = new AccessToken(memberId, authPropertiesForInvalidToken);

        validRefreshToken = jwtTokenProvider.createRefreshToken();
        expiredRefreshToken = new RefreshToken(authPropertiesForExpiredToken);
        invalidRefreshToken = new RefreshToken(authPropertiesForInvalidToken);
    }

    @DisplayName("액세스 토큰을 파싱한다.")
    @Nested
    class parseAccessToken {

        @DisplayName("만료되지 않은 액세스 토큰을 파싱한다.")
        @Test
        void parseUnexpiredAccessToken() {
            long actual = jwtTokenProvider.parseAccessToken(validAccessToken);

            assertThat(actual).isEqualTo(memberId);
        }

        @DisplayName("만료된 액세스 토큰을 파싱한다.")
        @Test
        void parseExpiredAccessToken() {
            long actual = jwtTokenProvider.parseAccessToken(expiredAccessToken);

            assertThat(actual).isEqualTo(memberId);
        }

        @DisplayName("유효하지 않은 액세스 토큰을 파싱하면 400 예외가 발생한다.")
        @Test
        void parseInvalidAccessTokenThrowException() {
            assertThatThrownBy(() -> jwtTokenProvider.parseAccessToken(invalidAccessToken))
                    .isInstanceOf(OdyBadRequestException.class);
        }
    }

    @TestInstance(Lifecycle.PER_CLASS)
    @DisplayName("액세스 토큰을 검증한다.")
    @Nested
    class validateAccessToken {

        @DisplayName("유효하고 만료되지 않은 액세스 토큰이면 예외가 발생하지 않는다.")
        @Test
        void validateAccessTokenSuccess() {
            assertThatCode(() -> jwtTokenProvider.validate(validAccessToken))
                    .doesNotThrowAnyException();
        }

        @DisplayName("유효하지 않거나 만료된 액세스 토큰이면 예외가 발생한다.")
        @ParameterizedTest
        @MethodSource("getExpiredOrInvalidAccessTokenArguments")
        void validateAccessTokenFail(AccessToken accessToken) {
            assertThatThrownBy(() -> jwtTokenProvider.validate(accessToken))
                    .isInstanceOf(OdyException.class);
        }

        private Stream<AccessToken> getExpiredOrInvalidAccessTokenArguments() {
            return Stream.of(expiredAccessToken, invalidAccessToken);
        }
    }

    @DisplayName("액세스 토큰이 만료되지 않았는지 확인한다.")
    @Nested
    class isAccessTokenUnexpired {

        @DisplayName("유효하고 만료되지 않은 액세스 토큰이면 True를 반환한다.")
        @Test
        void checkAccessTokenUnexpiredSuccess() {
            assertThat(jwtTokenProvider.isUnexpired(validAccessToken)).isTrue();
        }

        @DisplayName("만료된 액세스 토큰이면 False를 반환한다.")
        @Test
        void checkAccessTokenExpired() {
            assertThat(jwtTokenProvider.isUnexpired(expiredAccessToken)).isFalse();
        }

        @DisplayName("유효하지 않은 액세스 토큰이면 400 예외가 발생한다.")
        @Test
        void checkInvalidAccessTokenThrowException() {
            assertThatThrownBy(() -> jwtTokenProvider.validate(invalidAccessToken))
                    .isInstanceOf(OdyBadRequestException.class);
        }
    }

    @DisplayName("리프레시 토큰이 만료되지 않았는지 확인한다.")
    @Nested
    class isRefreshTokenUnexpired {

        @DisplayName("유효하고 만료되지 않은 리프레시 토큰이면 True를 반환한다.")
        @Test
        void checkRefreshTokenUnexpiredSuccess() {
            assertThat(jwtTokenProvider.isUnexpired(validRefreshToken)).isTrue();
        }

        @DisplayName("만료된 리프레시 토큰이면 False를 반환한다.")
        @Test
        void checkRefreshTokenExpired() {
            assertThat(jwtTokenProvider.isUnexpired(expiredRefreshToken)).isFalse();
        }

        @DisplayName("유효하지 않은 리프레시 토큰이면 400 예외가 발생한다.")
        @Test
        void checkInvalidRefreshTokenThrowException() {
            assertThatThrownBy(() -> jwtTokenProvider.isUnexpired(invalidRefreshToken))
                    .isInstanceOf(OdyBadRequestException.class);
        }
    }
}
