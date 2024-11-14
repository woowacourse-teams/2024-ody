package com.ody.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.ody.auth.service.KakaoAuthUnlinkClient;
import com.ody.auth.token.RefreshToken;
import com.ody.common.BaseServiceTest;
import com.ody.common.exception.OdyUnauthorizedException;
import com.ody.eta.repository.EtaRepository;
import com.ody.mate.domain.Mate;
import com.ody.mate.repository.MateRepository;
import com.ody.meeting.domain.Meeting;
import com.ody.member.domain.AuthProvider;
import com.ody.member.domain.DeviceToken;
import com.ody.member.domain.Member;
import com.ody.member.repository.MemberRepository;
import com.ody.notification.repository.NotificationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

class MemberServiceTest extends BaseServiceTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    MemberService memberService;

    @Autowired
    MateRepository mateRepository;

    @Autowired
    EtaRepository etaRepository;

    @Autowired
    NotificationRepository notificationRepository;

    @MockBean
    private KakaoAuthUnlinkClient kakaoAuthUnlinkClient;

    @DisplayName("특정 회원의 리프레시 토큰을 삭제할 수 있다")
    @Test
    void removeMemberRefreshToken() {
        Member member = saveMember("pid", "deviceToken", "refresh-token=token");

        memberService.updateRefreshToken(member.getId(), null);

        Member findMember = memberService.findById(member.getId());
        assertThat(findMember.getRefreshToken()).isNull();
    }

    @DisplayName("회원을 삭제한다.")
    @Test
    void delete() {
        Member member = fixtureGenerator.generateMember();
        Meeting meeting = fixtureGenerator.generateMeeting();
        Mate mate = fixtureGenerator.generateMate(meeting, member);
        fixtureGenerator.generateEta(mate);
        fixtureGenerator.generateNotification(mate);

        memberService.delete(member);

        Member actual = memberRepository.findById(member.getId()).get();
        assertThat(actual.getDeletedAt()).isNotNull();
    }

    @DisplayName("삭제 회원을 조회할 수 없다.")
    @Test
    void findDeletedMemberById() {
        Member member = fixtureGenerator.generateMember();
        memberService.delete(member);

        assertThatThrownBy(() -> memberService.findById(member.getId()))
                .isInstanceOf(OdyUnauthorizedException.class);
    }

    public Member saveMember(String providerId, String rawDeviceToken, String rawRefreshToken) {
        Member member = fixtureGenerator.generateSavedMember(providerId, rawDeviceToken);
        RefreshToken refreshToken = new RefreshToken(rawRefreshToken);
        member.updateRefreshToken(refreshToken);
        return memberRepository.save(member);
    }
}
