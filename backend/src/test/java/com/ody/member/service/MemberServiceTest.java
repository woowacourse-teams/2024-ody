package com.ody.member.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ody.auth.dto.request.AuthRequest;
import com.ody.common.BaseServiceTest;
import com.ody.common.exception.OdyBadRequestException;
import com.ody.member.domain.DeviceToken;
import com.ody.member.domain.Member;
import com.ody.member.repository.MemberRepository;
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
        Member member = new Member("providerId", "제리", "imageUrl", new DeviceToken("Bearer device-token=dt"));
        memberRepository.save(member);

        AuthRequest request = new AuthRequest(
                "Bearer device-token=dt",
                "providerId2",
                member.getNickname(),
                member.getImageUrl()
        );

        assertThatThrownBy(() -> memberService.save(request))
                .isInstanceOf(OdyBadRequestException.class)
                .hasMessage("중복된 디바이스 토큰이 존재합니다.");
    }

    @DisplayName("중복된 providerType, providerId가 있을 시, 예외가 발생한다")
    @Test
    void saveFailWhenDuplicatedAuthProvider() {
        String providerId = "providerId";
        Member member = new Member(providerId, "제리", "imageUrl", new DeviceToken("Bearer device-token=dt"));
        memberRepository.save(member);

        AuthRequest request = new AuthRequest(
                "Bearer device-token=dt2",
                providerId,
                member.getNickname(),
                member.getImageUrl()
        );

        assertThatThrownBy(() -> memberService.save(request))
                .isInstanceOf(OdyBadRequestException.class)
                .hasMessage(String.format("중복된 providerId(%s)입니다.", providerId));
    }
}
