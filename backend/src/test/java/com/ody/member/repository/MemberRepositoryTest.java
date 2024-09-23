package com.ody.member.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.ody.common.BaseRepositoryTest;
import com.ody.common.Fixture;
import com.ody.member.domain.DeviceToken;
import com.ody.member.domain.Member;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MemberRepositoryTest extends BaseRepositoryTest {

    @DisplayName("회원을 삭제(soft delete)한다.")
    @Test
    void deleteById() {
        Member member = fixtureGenerator.generateMember();

        memberRepository.deleteById(member.getId());

        entityManager.flush();
        Optional<Member> actual = memberRepository.findById(member.getId());
        assertThat(actual).isPresent();
        assertThat(actual.get().getDeletedAt()).isNotNull();
    }
}
