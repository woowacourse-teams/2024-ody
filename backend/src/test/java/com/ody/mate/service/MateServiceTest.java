package com.ody.mate.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

import com.ody.common.BaseServiceTest;
import com.ody.common.Fixture;
import com.ody.common.exception.OdyBadRequestException;
import com.ody.eta.domain.Eta;
import com.ody.eta.repository.EtaRepository;
import com.ody.mate.domain.Mate;
import com.ody.mate.domain.Nickname;
import com.ody.mate.dto.request.MateSaveRequestV2;
import com.ody.mate.dto.response.MateSaveResponseV2;
import com.ody.mate.repository.MateRepository;
import com.ody.meeting.domain.Meeting;
import com.ody.meeting.repository.MeetingRepository;
import com.ody.member.domain.DeviceToken;
import com.ody.member.domain.Member;
import com.ody.member.repository.MemberRepository;
import com.ody.util.TimeUtil;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

class MateServiceTest extends BaseServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private MateRepository mateRepository;

    @Autowired
    private EtaRepository etaRepository;

    @Autowired
    private MateService mateService;

    @DisplayName("회원이 참여하고 있는 특정 약속의 참여자 리스트를 조회한다.")
    @Test
    void findAllByMemberAndMeetingIdSuccess() {
        Member member1 = memberRepository.save(Fixture.MEMBER1);
        Member member2 = memberRepository.save(Fixture.MEMBER2);

        Meeting meeting = meetingRepository.save(Fixture.ODY_MEETING);

        Mate mate1 = mateRepository.save(new Mate(meeting, member1, new Nickname("조조"), Fixture.ORIGIN_LOCATION, 10L));
        Mate mate2 = mateRepository.save(new Mate(meeting, member2, new Nickname("제리"), Fixture.ORIGIN_LOCATION, 10L));

        List<Mate> mates = mateService.findAllByMemberAndMeetingId(member1, meeting.getId());
        List<Long> mateIds = mates.stream()
                .map(Mate::getId)
                .toList();

        assertThat(mateIds).containsOnly(mate1.getId(), mate2.getId());
    }

    @DisplayName("약속에 참여하고 있는 회원이 아니면 예외가 발생한다.")
    @Test
    void findAllByMemberAndMeetingIdException() {
        Member member1 = memberRepository.save(Fixture.MEMBER1);
        Member member2 = memberRepository.save(Fixture.MEMBER2);

        Meeting meeting = meetingRepository.save(Fixture.ODY_MEETING);
        mateRepository.save(new Mate(meeting, member1, new Nickname("조조"), Fixture.ORIGIN_LOCATION, 10L));

        assertThatThrownBy(() -> mateService.findAllByMemberAndMeetingId(member2, meeting.getId()))
                .isInstanceOf(OdyBadRequestException.class);
    }

    @DisplayName("약속이 1분 뒤이고 소요시간이 2분으로 Eta상태가 지각 위기인 mate를 콕 찌를 수 있다")
    @Test
    void nudgeSuccessWhenLateWarning() {
        long remainingTime = 1L;
        Meeting oneMinutesLaterMeeting = makeSavedMeetingByTime(TimeUtil.nowWithTrim().plusMinutes(remainingTime));
        Member member1 = memberRepository.save(Fixture.MEMBER1);
        Mate mate = new Mate(oneMinutesLaterMeeting, member1, new Nickname("콜리"), Fixture.ORIGIN_LOCATION, 10L);
        mate = mateRepository.save(mate);
        Eta lateWarningEta = etaRepository.save(new Eta(mate, remainingTime + 1L));

        mateService.nudge(mate.getId());

        Mockito.verify(getFcmPushSender(), times(1)).sendNudgeMessage(any());
    }

    @DisplayName("약속이 지금이고 소요시간이 2분으로 Eta상태가 지각인 mate를 콕 찌를 수 있다")
    @Test
    void nudgeSuccessWhenLate() {
        Meeting nowMeeting = makeSavedMeetingByTime(TimeUtil.nowWithTrim());
        Member member1 = memberRepository.save(Fixture.MEMBER1);
        Mate mate = new Mate(nowMeeting, member1, new Nickname("콜리"), Fixture.ORIGIN_LOCATION, 10L);
        mate = mateRepository.save(mate);
        Eta lateEta = etaRepository.save(new Eta(mate, 2L));

        mateService.nudge(mate.getId());

        Mockito.verify(getFcmPushSender(), times(1)).sendNudgeMessage(any());
    }

    @DisplayName("약속이 3분 뒤이고 소요시간이 2분으로 Eta상태가 도착 예정인 mate를 재촉하면 예외가 발생한다")
    @Test
    void nudgeFailWhenArriavalSoon() {
        long remainingTime = 3L;
        Meeting threeMinutesLaterMeeting = makeSavedMeetingByTime(TimeUtil.nowWithTrim().plusMinutes(remainingTime));
        Member member1 = memberRepository.save(Fixture.MEMBER1);
        Mate mate = new Mate(threeMinutesLaterMeeting, member1, new Nickname("콜리"), Fixture.ORIGIN_LOCATION, 10L);
        Mate savedMate = mateRepository.save(mate);
        Eta arrivalSoonEta = etaRepository.save(new Eta(mate, remainingTime - 1L));

        assertThatThrownBy(() -> mateService.nudge(savedMate.getId()))
                .isInstanceOf(OdyBadRequestException.class);
    }

    @DisplayName("3분 뒤 약속에 약속장소에 도착하여 Eta상태가 도착인 mate를 재촉하면 예외가 발생한다")
    @Test
    void nudgeFailWhenArrived() {
        long remainingTime = 3L;
        Meeting threeMinutesLaterMeeting = makeSavedMeetingByTime(TimeUtil.nowWithTrim().plusMinutes(remainingTime));
        Member member1 = memberRepository.save(Fixture.MEMBER1);
        Mate mate = new Mate(threeMinutesLaterMeeting, member1, new Nickname("콜리"), Fixture.TARGET_LOCATION, 10L);
        Mate savedMate = mateRepository.save(mate);
        Eta arrivalSoonEta = etaRepository.save(new Eta(mate, 0L));

        assertThatThrownBy(() -> mateService.nudge(savedMate.getId()))
                .isInstanceOf(OdyBadRequestException.class);
    }

    private Meeting makeSavedMeetingByTime(LocalDateTime time) {
        Meeting meeting = new Meeting(
                "오디",
                time.toLocalDate(),
                time.toLocalTime(),
                Fixture.TARGET_LOCATION,
                "초대코드"
        );
        return meetingRepository.save(meeting);
    }

    @DisplayName("참여자 생성")
    @Nested
    class saveAndSendNotifications {

        @DisplayName("하나의 약속에 동일한 닉네임을 가진 참여자가 존재할 수 있다.")
        @Test
        void saveMateWithDuplicateNickname() {
            String nickname = "제리";
            Member member1 = memberRepository.save(new Member(nickname, new DeviceToken("Bearer device-token=dt1")));
            Member member2 = memberRepository.save(new Member(nickname, new DeviceToken("Bearer device-token=dt2")));
            Meeting meeting = meetingRepository.save(Fixture.ODY_MEETING);
            Mate mate1 = mateRepository.save(new Mate(meeting, member1, Fixture.ORIGIN_LOCATION, 10L));

            MateSaveRequestV2 mateSaveRequest = new MateSaveRequestV2(
                    meeting.getInviteCode(),
                    Fixture.ORIGIN_LOCATION.getAddress(),
                    Fixture.ORIGIN_LOCATION.getLatitude(),
                    Fixture.ORIGIN_LOCATION.getLongitude()
            );
            MateSaveResponseV2 mateSaveResponse = mateService.saveAndSendNotifications(mateSaveRequest, member2,
                    meeting);

            assertThat(mateSaveResponse.meetingId()).isEqualTo(mate1.getId());
        }

        @DisplayName("하나의 약속에 동일한 회원이 존재할 수 없다.")
        @Test
        void saveMateWithDuplicateMember() {
            Member member = memberRepository.save(new Member("제리", new DeviceToken("Bearer device-token=dt1")));
            Meeting meeting = meetingRepository.save(Fixture.ODY_MEETING);
            mateRepository.save(new Mate(meeting, member, Fixture.ORIGIN_LOCATION, 10L));

            MateSaveRequestV2 mateSaveRequest = new MateSaveRequestV2(
                    meeting.getInviteCode(),
                    Fixture.ORIGIN_LOCATION.getAddress(),
                    Fixture.ORIGIN_LOCATION.getLatitude(),
                    Fixture.ORIGIN_LOCATION.getLongitude()
            );
            assertThatThrownBy(() -> mateService.saveAndSendNotifications(mateSaveRequest, member, meeting))
                    .isInstanceOf(OdyBadRequestException.class);
        }
    }
}
