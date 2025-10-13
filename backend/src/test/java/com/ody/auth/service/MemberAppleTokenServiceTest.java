package com.ody.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ody.auth.domain.MemberAppleToken;
import com.ody.common.BaseServiceTest;
import com.ody.common.exception.OdyNotFoundException;
import com.ody.member.domain.AuthProvider;
import com.ody.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MemberAppleTokenServiceTest extends BaseServiceTest {

    @Autowired
    private MemberAppleTokenService memberAppleTokenService;

    @DisplayName("memberId로 AppleRefreshToken을 조회한다.")
    @Test
    void findAppleRefreshTokenSuccess() {
        MemberAppleToken memberAppleToken = fixtureGenerator.generateMemberAppleToken();
        Member member = memberAppleToken.getMember();
        AuthProvider authProvider = member.getAuthProvider();

        MemberAppleToken appleRefreshToken = memberAppleTokenService.findByMemberId(member.getId());

        assertThat(appleRefreshToken.getAppleRefreshToken()).isEqualTo(memberAppleToken.getAppleRefreshToken());
    }

    @DisplayName("memberId로 AppleRefreshToken을 조회할 수 없으면 예외가 발생한다.")
    @Test
    void findAppleRefreshTokenException() {
        assertThatThrownBy(() -> memberAppleTokenService.findByMemberId(1L))
                .isInstanceOf(OdyNotFoundException.class);
    }
}
