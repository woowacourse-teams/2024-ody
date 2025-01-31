package com.ody.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ody.common.BaseServiceTest;
import com.ody.common.exception.OdyNotFoundException;
import com.ody.member.domain.AuthProvider;
import com.ody.member.domain.MemberAppleToken;
import com.ody.member.domain.ProviderType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MemberAppleTokenServiceTest extends BaseServiceTest {

    @Autowired
    private MemberAppleTokenService memberAppleTokenService;

    @DisplayName("AuthProvider로 AppleRefreshToken을 조회한다.")
    @Test
    void findAppleRefreshTokenSuccess() {
        MemberAppleToken memberAppleToken = fixtureGenerator.generateMemberAppleToken();

        AuthProvider authProvider = memberAppleToken.getMember().getAuthProvider();
        String appleRefreshToken = memberAppleTokenService.findAppleRefreshToken(authProvider);

        assertThat(appleRefreshToken).isEqualTo(memberAppleToken.getAppleRefreshToken());
    }

    @DisplayName("AuthProvider로 AppleRefreshToken을 조회할 수 없으면 예외가 발생한다.")
    @Test
    void findAppleRefreshTokenException() {
        AuthProvider authProvider = new AuthProvider(ProviderType.APPLE, "wrong-pid");

        assertThatThrownBy(() -> memberAppleTokenService.findAppleRefreshToken(authProvider))
                .isInstanceOf(OdyNotFoundException.class);
    }
}
