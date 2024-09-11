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
    class LogoutTest {

        @DisplayName("성공 : 유효한 액세스 토큰")
        @Test
        void logoutSuccess() {
            RefreshToken validRefreshToken = TokenFixture.getValidRefreshToken();
            Member member = createMemberByRefreshToken(validRefreshToken);
            AccessToken validAccessToken = TokenFixture.getValidAccessToken(member.getId());

            String rawAccessTokenValue = resolveRawAccessToken(validAccessToken);

            assertThatCode(() -> authService.logout(rawAccessTokenValue))
                    .doesNotThrowAnyException();
        }

        @DisplayName("회원이 이미 로그아웃 상태더라도 200을 반환한다")
        @Test
        void logoutSuccessWhenAlreadyLogout() {
            Member logoutMember = createMemberByRefreshToken(null);
            AccessToken validAccessToken = TokenFixture.getValidAccessToken(logoutMember.getId());

            String rawAccessTokenValue = resolveRawAccessToken(validAccessToken);

            assertThatCode(() -> authService.logout(rawAccessTokenValue))
                    .doesNotThrowAnyException();
        }

        @DisplayName("실패 : 만료된 액세스 토큰으로 로그아웃 시도 시 401을 반환한다")
        @Test
        void logoutFailWhenExpiredAccessToken() {
            RefreshToken validRefreshToken = TokenFixture.getValidRefreshToken();
            Member member = createMemberByRefreshToken(validRefreshToken);
            AccessToken expiredAccessToken = TokenFixture.getExpiredAccessToken(member.getId());
            String rawAccessTokenValue = resolveRawAccessToken(expiredAccessToken);

            assertThatThrownBy(() -> authService.logout(rawAccessTokenValue))
                    .isInstanceOf(OdyUnauthorizedException.class);
        }

        private String resolveRawAccessToken(AccessToken accessToken) {
            return "Bearer access-token=" + accessToken.getValue();
        }

        private Member createMemberByRefreshToken(RefreshToken refreshToken) {
            Member member = new Member("pid", "콜리", "imageUrl", new DeviceToken("deviceToken"));
            member.updateRefreshToken(refreshToken);
            return memberRepository.save(member);
        }
    }
}
