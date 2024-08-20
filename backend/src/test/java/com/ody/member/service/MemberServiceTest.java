package com.ody.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ody.auth.dto.request.AuthRequest;
import com.ody.common.BaseServiceTest;
import com.ody.common.exception.OdyBadRequestException;
import com.ody.member.domain.DeviceToken;
import com.ody.member.domain.Member;
import com.ody.member.repository.MemberRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MemberServiceTest extends BaseServiceTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    MemberService memberService;

    @DisplayName("중복된 디바이스 토큰이 있을 시, 예외가 발생한다")
    @Test
    void saveFailWhenDuplicatedDeviceToken() {
        Member member = new Member("providerId", "제리", "imageUrl", new DeviceToken("dt"));
        memberRepository.save(member);

        Member duplicateDeviceTokenMember = new Member("providerId2", "제리", "imageUrl2", new DeviceToken("dt"));

        assertThatThrownBy(() -> memberService.save(duplicateDeviceTokenMember))
                .isInstanceOf(OdyBadRequestException.class)
                .hasMessage("중복된 디바이스 토큰이 존재합니다.");
    }

    @Disabled
    @DisplayName("일치하는 providerType, providerId가 있을 시, 해당 멤버가 반환된다.")
    @Test
    void saveFailWhenDuplicatedAuthProvider() {
        String providerId = "providerId";
        Member member = new Member(providerId, "제리", "imageUrl", new DeviceToken("dt1"));
        Member savedMember = memberRepository.save(member);

        Member actual = memberService.save(savedMember);

        assertThat(actual.getId()).isEqualTo(savedMember.getId());
    }
}
