package com.ody.meeting.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.ody.common.BaseServiceTest;
import com.ody.common.Fixture;
import com.ody.common.exception.OdyNotFoundException;
import com.ody.mate.repository.MateRepository;
import com.ody.meeting.domain.Meeting;
import com.ody.meeting.dto.request.MeetingSaveRequestV1;
import com.ody.meeting.dto.response.MateResponse;
import com.ody.meeting.dto.response.MeetingSaveResponseV1;
import com.ody.meeting.dto.response.MeetingWithMatesResponse;
import com.ody.meeting.repository.MeetingRepository;
import com.ody.member.domain.Member;
import com.ody.member.repository.MemberRepository;
import com.ody.util.InviteCodeGenerator;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MeetingServiceTest extends BaseServiceTest {

    @Autowired
    private MeetingService meetingService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private MateRepository mateRepository;

    @DisplayName("약속 저장 및 초대 코드 갱신에 성공한다")
    @Test
    void saveV1Success() {
        Meeting testMeeting = Fixture.ODY_MEETING1;
        MeetingSaveRequestV1 request = new MeetingSaveRequestV1(
                testMeeting.getName(),
                testMeeting.getDate(),
                testMeeting.getTime(),
                testMeeting.getTarget().getAddress(),
                testMeeting.getTarget().getLatitude(),
                testMeeting.getTarget().getLongitude()
        );

        MeetingSaveResponseV1 response = meetingService.saveV1(request);

        assertAll(
                () -> assertThat(response.name()).isEqualTo(request.name()),
                () -> assertThat(response.date()).isEqualTo(request.date()),
                () -> assertThat(response.time()).isEqualTo(request.time()),
                () -> assertThat(response.targetAddress()).isEqualTo(request.targetAddress()),
                () -> assertThat(response.targetLatitude()).isEqualTo(request.targetLatitude()),
                () -> assertThat(response.targetLongitude()).isEqualTo(request.targetLongitude()),
                () -> assertThat(InviteCodeGenerator.decode(response.inviteCode())).isEqualTo(response.id())
        );
    }

    @DisplayName("약속과 참여자들 정보를 조회한다.")
    @Test
    void findMeetingWithMatesSuccess() {
        Member member1 = memberRepository.save(Fixture.MEMBER1);
        memberRepository.save(Fixture.MEMBER2);

        Long meetingId = meetingRepository.save(Fixture.ODY_MEETING1).getId();

        mateRepository.save(Fixture.MATE1);
        mateRepository.save(Fixture.MATE2);

        MeetingWithMatesResponse meetingWithMatesResponses = meetingService.findMeetingWithMates(member1, meetingId);
        List<String> mateNicknames = meetingWithMatesResponses.mates().stream()
                .map(MateResponse::nickname)
                .toList();

        assertAll(
                () -> assertThat(meetingWithMatesResponses.id()).isEqualTo(meetingId),
                () -> assertThat(mateNicknames).containsOnly(
                        Fixture.MATE1.getNicknameValue(),
                        Fixture.MATE2.getNicknameValue()
                )
        );
    }

    @DisplayName("약속 조회 시, 약속이 존재하지 않으면 예외가 발생한다.")
    @Test
    void findMeetingWithMatesException() {
        Member member = memberRepository.save(Fixture.MEMBER1);

        assertThatThrownBy(() -> meetingService.findMeetingWithMates(member, 1L))
                .isInstanceOf(OdyNotFoundException.class);
    }
}
