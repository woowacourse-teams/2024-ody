package com.ody.auth.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ody.auth.token.AccessToken;
import com.ody.auth.token.RefreshToken;
import com.ody.common.BaseServiceTest;
import com.ody.common.TokenFixture;
import com.ody.common.exception.OdyBadRequestException;
import com.ody.common.exception.OdyUnauthorizedException;
import com.ody.member.domain.DeviceToken;
import com.ody.member.domain.Member;
import com.ody.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class AuthServiceTest extends BaseServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("로그아웃 테스트")
    @Nested
    class LogOutTest {

        @DisplayName("성공 : 유효한 액세스 토큰")
        @Test
        void logoutSuccess() {
            RefreshToken validRefreshToken = TokenFixture.getValidRefreshToken();
            Member member = createMemberByRefreshToken(validRefreshToken);
            AccessToken validAccessToken = TokenFixture.getValidAccessToken(member.getId());

            String authorizationHeader = resolveAuthorizationHeader(validAccessToken, validRefreshToken);

            assertThatCode(() -> authService.logout(authorizationHeader))
                    .doesNotThrowAnyException();
        }

        @DisplayName("실패 : 만료된 액세스 토큰으로 로그아웃 시도 시 401을 반환한다")
        @Test
        void logoutFailWhenExpiredAccessToken() {
            RefreshToken validRefreshToken = TokenFixture.getValidRefreshToken();
            Member member = createMemberByRefreshToken(validRefreshToken);
            AccessToken expiredAccessToken = TokenFixture.getExpiredAccessToken(member.getId());
            String authorizationHeader = resolveAuthorizationHeader(expiredAccessToken, validRefreshToken);

            assertThatThrownBy(() -> authService.logout(authorizationHeader))
                    .isInstanceOf(OdyUnauthorizedException.class);
        }

        @DisplayName("실패 : 이미 로그아웃한 유저 엑세스 토큰으로 로그아웃 시도 시 400을 반환한다")
        @Test
        void logoutFailWhenTryAlreadyLogoutMember() {
            RefreshToken validRefreshToken = TokenFixture.getValidRefreshToken();
            Member member = createMemberByRefreshToken(validRefreshToken);
            AccessToken validAccessToken = TokenFixture.getValidAccessToken(member.getId());
            String authorizationHeader = resolveAuthorizationHeader(validAccessToken, validRefreshToken);

            member.updateRefreshToken(null);

            assertThatThrownBy(() -> authService.logout(authorizationHeader))
                    .isInstanceOf(OdyBadRequestException.class);
        }

        private String resolveAuthorizationHeader(AccessToken accessToken, RefreshToken refreshToken) {
            return "Bearer access-token=" + accessToken.getValue() + " refresh-token=" + refreshToken.getValue();
        }

        private Member createMemberByRefreshToken(RefreshToken refreshToken) {
            Member member = new Member("pid", "콜리", "imageUrl", new DeviceToken("deviceToken"));
            member.updateRefreshToken(refreshToken);
            return memberRepository.save(member);
        }
    }
}
