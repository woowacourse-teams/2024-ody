package com.ody.member.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.ody.common.Fixture;
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
    void findByDeviceToken() {
        Member member = memberRepository.save(Fixture.MEMBER1);

        Member findMember = memberRepository.findByDeviceToken(member.getDeviceToken()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
    }
}
