package com.ody.auth.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.ody.auth.domain.MemberAppleToken;
import com.ody.common.BaseRepositoryTest;
import com.ody.member.domain.AuthProvider;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MemberAppleTokenRepositoryTest extends BaseRepositoryTest {

    @DisplayName("AuthProvider로 MemberAppleToken을 조회한다.")
    @Test
    void findByMember_AuthProvider() {
        MemberAppleToken memberAppleToken = fixtureGenerator.generateMemberAppleToken();

        AuthProvider authProvider = memberAppleToken.getMember().getAuthProvider();
        Optional<MemberAppleToken> result = memberAppleTokenRepository.findByMember_AuthProvider(authProvider);

        assertThat(result).isPresent();
        assertThat(result.get().getAppleRefreshToken()).isEqualTo(memberAppleToken.getAppleRefreshToken());
    }

    @DisplayName("AuthProvider로 MemberAppleToken을 삭제한다.")
    @Test
    void deleteByMember_AuthProvider() {
        MemberAppleToken memberAppleToken = fixtureGenerator.generateMemberAppleToken();

        AuthProvider authProvider = memberAppleToken.getMember().getAuthProvider();
        memberAppleTokenRepository.deleteByMember_AuthProvider(authProvider);

        Optional<MemberAppleToken> result = memberAppleTokenRepository.findByMember_AuthProvider(authProvider);
        assertThat(result).isEmpty();
    }
}
