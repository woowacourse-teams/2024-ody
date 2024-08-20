package com.ody.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ody.auth.token.AccessToken;
import com.ody.auth.token.RefreshToken;
import com.ody.common.BaseServiceTest;
import com.ody.common.exception.OdyBadRequestException;
import com.ody.member.domain.AuthProvider;
import com.ody.member.domain.DeviceToken;
import com.ody.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

//@EnableAutoConfiguration
//@EnableConfigurationProperties(AuthProperties.class)
class JwtTokenProviderTest extends BaseServiceTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    //    @Disabled
    @DisplayName("AccesToken에 ")
    @Test
    void parseAccessToken() { // TODO: jwt properties 설정
        Member member = new Member(1L, new AuthProvider("providerId"), "콜리", "imageurl",
                new DeviceToken("dt"), new RefreshToken("refresh-token=rt"));
        AccessToken accessToken = jwtTokenProvider.createAccessToken(member.getId());

        long actual = jwtTokenProvider.parseAccessToken(accessToken);

        assertThat(actual).isEqualTo(member.getId());
    }

    @DisplayName("액세스 토큰이 만료되었다면 false를 반환한다.")
    @Test
    void isExpired() {
        AuthProperties authProperties = new AuthProperties(
                jwtTokenProvider.getAuthProperties().getAccessKey(),
                jwtTokenProvider.getAuthProperties().getRefreshKey(),
                0,
                0
        );
        AccessToken accessToken = new AccessToken(1L, authProperties);

        boolean actual = jwtTokenProvider.isUnexpired(accessToken);

        assertThat(actual).isFalse();
    }

    @DisplayName("액세스 토큰이 만료되지 않았다면 true를 반환한다.")
    @Test
    void isUnexpired() {
        AuthProperties authProperties = new AuthProperties(
                jwtTokenProvider.getAuthProperties().getAccessKey(),
                jwtTokenProvider.getAuthProperties().getRefreshKey(),
                60000,
                0
        );
        AccessToken accessToken = new AccessToken(1L, authProperties);

        boolean actual = jwtTokenProvider.isUnexpired(accessToken);

        assertThat(actual).isTrue();
    }

    @DisplayName("액세스 토큰이 유효하지 않다면 예외를 발생한다.")
    @Test
    void invalidAccessTokenThrowException() {
        AuthProperties authProperties = new AuthProperties(
                "wrong-access-token-key",
                jwtTokenProvider.getAuthProperties().getRefreshKey(),
                60000,
                0
        );
        AccessToken accessToken = new AccessToken(1L, authProperties);

        assertThatThrownBy(() -> jwtTokenProvider.isUnexpired(accessToken))
                .isInstanceOf(OdyBadRequestException.class);
    }

    @DisplayName("만료된 액세스 토큰을 파싱한다.")
    @Test
    void parseExpiredAccessToken() {
        AuthProperties authProperties = new AuthProperties(
                jwtTokenProvider.getAuthProperties().getAccessKey(),
                jwtTokenProvider.getAuthProperties().getRefreshKey(),
                0,
                0
        );
        long memberId = 1L;
        AccessToken accessToken = new AccessToken(memberId, authProperties);

        long actual = jwtTokenProvider.parseAccessToken(accessToken);

        assertThat(actual).isEqualTo(memberId);
    }
}
