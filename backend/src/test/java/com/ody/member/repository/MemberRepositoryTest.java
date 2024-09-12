package com.ody.member.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.ody.common.BaseRepositoryTest;
import com.ody.common.Fixture;
import com.ody.member.domain.DeviceToken;
import com.ody.member.domain.Member;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MemberRepositoryTest extends BaseRepositoryTest {

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

    @DisplayName("회원을 삭제(soft delete)한다.")
    @Test
    void delete() {
        Member member = fixtureGenerator.generateMember();

        memberRepository.delete(member);

        Member actual = (Member) entityManager.createNativeQuery("select * from Member where id = ?", Member.class)
                .setParameter(1, member.getId())
                .getSingleResult();
        assertThat(actual.getDeletedAt()).isNotNull();
    }

    @DisplayName("삭제된 회원은 조회하지 않는다.")
    @Test
    void doNotFindDeletedMember() {
        Member member = fixtureGenerator.generateMember();

        memberRepository.delete(member);

        Optional<Member> actual = memberRepository.findById(member.getId());
        assertThat(actual).isNotPresent();
    }
}
