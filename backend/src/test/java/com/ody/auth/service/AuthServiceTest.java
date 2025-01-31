package com.ody.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

import com.ody.auth.dto.request.AppleAuthRequest;
import com.ody.auth.dto.request.KakaoAuthRequest;
import com.ody.auth.token.AccessToken;
import com.ody.auth.token.RefreshToken;
import com.ody.common.BaseServiceTest;
import com.ody.common.TokenFixture;
import com.ody.common.exception.OdyUnauthorizedException;
import com.ody.member.domain.AuthProvider;
import com.ody.member.domain.DeviceToken;
import com.ody.member.domain.Member;
import com.ody.member.domain.ProviderType;
import com.ody.member.repository.MemberAppleTokenRepository;
import com.ody.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

class AuthServiceTest extends BaseServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private MemberRepository memberRepository;

    @MockBean
    private AppleValidateTokenClient appleValidateTokenClient;

    @Autowired
    private MemberAppleTokenRepository memberAppleTokenRepository;

    @DisplayName("멤버 인증 테스트")
    @Nested
    class AuthTest {

        @DisplayName("로그인 이력이 있는 기기로 비회원이 회원 생성을 시도하면 기기 이력을 삭제하고 회원을 생성한다.")
        @Test
        void saveMemberWhenNonMemberAttemptsWithLoggedInDevice() {
            fixtureGenerator.generateSavedMember("pid", "deviceToken");
            Member sameDeivceFreshMember = fixtureGenerator.generateUnsavedMember("newPid", "deviceToken");
            KakaoAuthRequest sameDeviceFreshMemberRequest = dtoGenerator.generateKakaoAuthRequest(sameDeivceFreshMember);

            authService.authenticate(sameDeviceFreshMemberRequest);

            assertAll(
                    () -> assertThat(getDeviceTokenByAuthProvider("pid")).isNull(),
                    () -> assertThat(getDeviceTokenByAuthProvider("newPid").getValue()).isEqualTo("deviceToken")
            );
        }

        @DisplayName("로그인 이력이 있는 기기로 동일 회원이 회원 생성을 시도하면 회원을 생성하지 않는다.")
        @Test
        void saveMemberWhenMemberAttemptsWithLoggedInDevice() {
            fixtureGenerator.generateSavedMember("pid", "deviceToken");
            Member sameMember = fixtureGenerator.generateUnsavedMember("pid", "deviceToken");
            KakaoAuthRequest sameMemberRequest = dtoGenerator.generateKakaoAuthRequest(sameMember);

            authService.authenticate(sameMemberRequest);

            assertThat(getDeviceTokenByAuthProvider("pid").getValue()).isEqualTo("deviceToken");
        }

        @DisplayName("로그인 이력이 있는 기기로 타 회원이 회원 생성을 시도하면 기기 이력을 이전한다.")
        @Test
        void saveMemberWhenOtherMemberAttemptsWithLoggedInDevice() {
            fixtureGenerator.generateSavedMember("pid", "deviceToken");
            fixtureGenerator.generateSavedMember("otherPid", "otherDeviceToken");
            Member otherPidSameDeviceUser = fixtureGenerator.generateUnsavedMember("otherPid", "deviceToken");
            KakaoAuthRequest otherPidSameDeviceUserRequest = dtoGenerator.generateKakaoAuthRequest(otherPidSameDeviceUser);

            authService.authenticate(otherPidSameDeviceUserRequest);

            assertAll(
                    () -> assertThat(getDeviceTokenByAuthProvider("pid")).isNull(),
                    () -> assertThat(getDeviceTokenByAuthProvider("otherPid").getValue()).isEqualTo("deviceToken")
            );
        }

        @DisplayName("로그인 이력이 없는 기기로 비회원이 회원 생성을 시도하면 회원을 생성한다.")
        @Test
        void saveMemberWhenNonMemberAttemptsWithUnloggedDevice() {
            fixtureGenerator.generateSavedMember("pid", "deviceToken");
            Member freshDeivceFreshPidMember = fixtureGenerator.generateUnsavedMember("newPid", "newDeviceToken");
            KakaoAuthRequest freshDeviceFreshPidMemberRequest = dtoGenerator.generateKakaoAuthRequest(freshDeivceFreshPidMember);

            authService.authenticate(freshDeviceFreshPidMemberRequest);

            assertAll(
                    () -> assertThat(getDeviceTokenByAuthProvider("pid").getValue()).isEqualTo("deviceToken"),
                    () -> assertThat(getDeviceTokenByAuthProvider("newPid").getValue()).isEqualTo("newDeviceToken")
            );
        }

        @DisplayName("로그인 이력이 없는 기기로 회원이 회원 생성을 시도하면 기기 이력을 변경한다.")
        @Test
        void saveMemberWhenMemberAttemptsWithUnloggedDevice() {
            fixtureGenerator.generateSavedMember("pid", "deviceToken");
            Member freshDeivceSamePidMember = fixtureGenerator.generateUnsavedMember("pid", "newDeviceToken");
            KakaoAuthRequest freshDeviceSamePidRequest = dtoGenerator.generateKakaoAuthRequest(freshDeivceSamePidMember);

            authService.authenticate(freshDeviceSamePidRequest);

            assertThat(getDeviceTokenByAuthProvider("pid").getValue()).isEqualTo("newDeviceToken");
        }

        private DeviceToken getDeviceTokenByAuthProvider(String providerId) {
            return memberRepository.findByAuthProvider(new AuthProvider(providerId)).get().getDeviceToken();
        }
    }

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
            Member member = fixtureGenerator.generateMember();
            member.updateRefreshToken(refreshToken);
            return memberRepository.save(member);
        }
    }

    @DisplayName("애플 회원 로그인 시 Apple Refresh Token을 저장한다.")
    @Test
    void authenticateSuccess() {
        String appleRefreshToken = "sample-apple-refresh-token";
        AppleAuthRequest appleAuthRequest = dtoGenerator.generateAppleAuthRequest();
        String authorizationCode = appleAuthRequest.getAuthorizationCode();
        when(appleValidateTokenClient.obtainRefreshToken(authorizationCode)).thenReturn(appleRefreshToken);

        authService.authenticate(appleAuthRequest);

        String result = getAppleRefreshTokenByProviderId(appleAuthRequest.getProviderId());
        assertThat(result).isEqualTo(appleRefreshToken);
    }

    private String getAppleRefreshTokenByProviderId(String providerId) {
        AuthProvider authProvider = new AuthProvider(ProviderType.APPLE, providerId);
        return memberAppleTokenRepository.findByMember_AuthProvider(authProvider).get().getAppleRefreshToken();
    }
}
