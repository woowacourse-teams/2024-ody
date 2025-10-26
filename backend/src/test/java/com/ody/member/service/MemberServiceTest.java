package com.ody.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.ody.auth.token.RefreshToken;
import com.ody.common.BaseServiceTest;
import com.ody.common.exception.OdyUnauthorizedException;
import com.ody.eta.repository.EtaRepository;
import com.ody.mate.domain.Mate;
import com.ody.mate.repository.MateRepository;
import com.ody.meeting.domain.Meeting;
import com.ody.member.domain.Member;
import com.ody.member.domain.ProviderType;
import com.ody.member.repository.MemberRepository;
import com.ody.notification.repository.NotificationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

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

        memberService.deleteV2(member);

        Member actual = memberRepository.findById(member.getId()).get();
        assertThat(actual.getDeletedAt()).isNotNull();
    }

    @DisplayName("카카오 회원 탈퇴 시 카카오 클라이언트가 호출된다.")
    @Test
    void deleteUsingKakaoClient() {
        Member member = fixtureGenerator.generateMember(ProviderType.KAKAO);

        memberService.deleteV2(member);

        verify(kakaoAuthUnlinkClient, times(1)).unlink(member.getAuthProvider().getProviderId());
    }

    @DisplayName("애플 회원 탈퇴 시 애플 클라이언트가 호출된다.")
    @Test
    void deleteUsingAppleClient() {
        Member member = fixtureGenerator.generateMember(ProviderType.APPLE);

        memberService.deleteV2(member);

        verify(appleRevokeTokenClient, times(1)).unlink(member.getAuthProvider().getProviderId());
    }

    @DisplayName("삭제 회원을 조회할 수 없다.")
    @Test
    void findDeletedMemberById() {
        Member member = fixtureGenerator.generateMember();
        memberService.deleteV2(member);

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
