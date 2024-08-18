package com.ody.member.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.ody.common.Fixture;
import com.ody.member.domain.DeviceToken;
import com.ody.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("기기 토큰으로 회원을 조회한다")
    @Test
    void findFirstByDeviceToken() {
        Member member = memberRepository.save(Fixture.MEMBER1);

        Member findMember = memberRepository.findFirstByDeviceToken(member.getDeviceToken()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
    }

    @DisplayName("임의의 providerType, providerId를 가진 회원이 존재하는지 조회한다.")
    @Test
    void existsByAuthProvider() {
        Member member = new Member("12345", "몽키건우", "imageurl", new DeviceToken("Bearer device-token=dt"));
        memberRepository.save(member);

        boolean actual = memberRepository.existsByAuthProvider(member.getAuthProvider());

        assertThat(actual).isTrue();
    }
}
