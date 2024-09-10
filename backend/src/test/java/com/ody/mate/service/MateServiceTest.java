package com.ody.mate.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

import com.ody.common.BaseServiceTest;
import com.ody.common.Fixture;
import com.ody.common.FixtureGenerator;
import com.ody.common.exception.OdyBadRequestException;
import com.ody.common.exception.OdyNotFoundException;
import com.ody.eta.domain.Eta;
import com.ody.eta.repository.EtaRepository;
import com.ody.mate.domain.Mate;
import com.ody.mate.domain.Nickname;
import com.ody.mate.dto.request.MateSaveRequestV2;
import com.ody.mate.dto.request.NudgeRequest;
import com.ody.mate.dto.response.MateSaveResponseV2;
import com.ody.mate.repository.MateRepository;
import com.ody.meeting.domain.Location;
import com.ody.meeting.domain.Meeting;
import com.ody.meeting.repository.MeetingRepository;
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

    @Autowired
    private FixtureGenerator fixtureGenerator;

    @DisplayName("회원이 참여하고 있는 특정 약속의 참여자 리스트를 조회한다.")
    @Test
    void findAllByMemberAndMeetingIdSuccess() {
        Member member1 = memberRepository.save(Fixture.MEMBER1);
        Member member2 = memberRepository.save(Fixture.MEMBER2);

        Meeting meeting = meetingRepository.save(Fixture.ODY_MEETING);

        Mate mate1 = mateRepository.save(new Mate(meeting, member1, new Nickname("조조"), Fixture.ORIGIN_LOCATION, 10L));
        Mate mate2 = mateRepository.save(new Mate(meeting, member2, new Nickname("제리"), Fixture.ORIGIN_LOCATION, 10L));

        List<Mate> mates = mateService.findAllByMeetingIdIfMate(member1, meeting.getId());
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

        assertThatThrownBy(() -> mateService.findAllByMeetingIdIfMate(member2, meeting.getId()))
                .isInstanceOf(OdyNotFoundException.class);
    }

    @DisplayName("재촉하기 테스트")
    @Nested
    class NudgeTest {

        @DisplayName("약속이 1분 뒤이고 소요시간이 2분으로 Eta상태가 지각 위기인 mate를 재촉할 수 있다")
        @Test
        void nudgeSuccessWhenLateWarning() {
            Meeting oneMinuteLaterMeeting = makeSavedMeetingByRemainingMinutes(1L);
            Mate requestMate = makeRequestMate(oneMinuteLaterMeeting);
            Mate nudgedLateWarningMate = makeNudgedMate(oneMinuteLaterMeeting, Fixture.ORIGIN_LOCATION);
            Eta lateWarningEta = etaRepository.save(new Eta(nudgedLateWarningMate, 2L));

            NudgeRequest nudgeRequest = new NudgeRequest(requestMate.getId(), nudgedLateWarningMate.getId());
            mateService.nudge(nudgeRequest);

            Mockito.verify(getFcmPushSender(), times(1)).sendNudgeMessage(any(), any());
        }

        @DisplayName("약속이 지금이고 소요시간이 2분으로 Eta상태가 지각인 mate를 재촉할 수 있다")
        @Test
        void nudgeSuccessWhenLate() {
            Meeting nowMeeting = makeSavedMeetingByRemainingMinutes(0L);
            Mate requestMate = makeRequestMate(nowMeeting);
            Mate nudgedLateMate = makeNudgedMate(nowMeeting, Fixture.ORIGIN_LOCATION);
            Eta lateEta = etaRepository.save(new Eta(nudgedLateMate, 2L));

            NudgeRequest nudgeRequest = new NudgeRequest(requestMate.getId(), nudgedLateMate.getId());
            mateService.nudge(nudgeRequest);

            Mockito.verify(getFcmPushSender(), times(1)).sendNudgeMessage(any(), any());
        }

        @DisplayName("같은 약속 참여자가 아니라면 재촉할 수 없다")
        @Test
        void nudgedFailedWhenDifferentMeetingAttender() {
            Meeting meeting1 = makeSavedMeetingByRemainingMinutes(1L);
            Meeting meeting2 = makeSavedMeetingByRemainingMinutes(1L);
            Mate requestMate = makeRequestMate(meeting1);
            Mate nudgedMate = makeNudgedMate(meeting2, Fixture.ORIGIN_LOCATION);

            NudgeRequest nudgeRequest = new NudgeRequest(requestMate.getId(), nudgedMate.getId());

            assertThatThrownBy(() -> mateService.nudge(nudgeRequest))
                    .isInstanceOf(OdyBadRequestException.class);
        }

        @DisplayName("약속이 3분 뒤이고 소요시간이 2분으로 Eta상태가 도착 예정인 mate를 재촉하면 예외가 발생한다")
        @Test
        void nudgeFailWhenArriavalSoon() {
            Meeting threeMinutesLaterMeeting = makeSavedMeetingByRemainingMinutes(3L);
            Mate requestMate = makeRequestMate(threeMinutesLaterMeeting);
            Mate nudgedArriavalSoonMate = makeNudgedMate(threeMinutesLaterMeeting, Fixture.ORIGIN_LOCATION);

            NudgeRequest nudgeRequest = new NudgeRequest(requestMate.getId(), nudgedArriavalSoonMate.getId());
            Eta arrivalSoonEta = etaRepository.save(new Eta(nudgedArriavalSoonMate, 2L));

            assertThatThrownBy(() -> mateService.nudge(nudgeRequest))
                    .isInstanceOf(OdyBadRequestException.class);
        }

        @DisplayName("3분 뒤 약속에 약속장소에 도착하여 Eta상태가 도착인 mate를 재촉하면 예외가 발생한다")
        @Test
        void nudgeFailWhenArrived() {
            Meeting threeMinutesLaterMeeting = makeSavedMeetingByRemainingMinutes(3L);
            Mate requestMate = makeRequestMate(threeMinutesLaterMeeting);
            Mate nudgedArrivedMate = makeNudgedMate(threeMinutesLaterMeeting, Fixture.TARGET_LOCATION);
            Eta arrivedEta = etaRepository.save(new Eta(nudgedArrivedMate, 0L));

            NudgeRequest nudgeRequest = new NudgeRequest(requestMate.getId(), nudgedArrivedMate.getId());

            assertThatThrownBy(() -> mateService.nudge(nudgeRequest))
                    .isInstanceOf(OdyBadRequestException.class);
        }

        private Meeting makeSavedMeetingByRemainingMinutes(long remainingMinutes) {
            LocalDateTime time = TimeUtil.nowWithTrim().plusMinutes(remainingMinutes);
            Meeting meeting = new Meeting(
                    "오디",
                    time.toLocalDate(),
                    time.toLocalTime(),
                    Fixture.TARGET_LOCATION,
                    "초대코드"
            );
            return meetingRepository.save(meeting);
        }

        private Mate makeRequestMate(Meeting meeting) {
            Member member = memberRepository.save(Fixture.MEMBER1);
            Mate requestMate = new Mate(meeting, member, new Nickname("제리"), Fixture.ORIGIN_LOCATION, 10L);
            return mateRepository.save(requestMate);
        }

        private Mate makeNudgedMate(Meeting meeting, Location origin) {
            Member member = memberRepository.save(Fixture.MEMBER2);
            Mate requestMate = new Mate(meeting, member, new Nickname("콜리"), origin, 10L);
            return mateRepository.save(requestMate);
        }
    }

    @DisplayName("참여자 생성")
    @Nested
    class saveAndSendNotifications {

        @DisplayName("하나의 약속에 동일한 닉네임을 가진 참여자가 존재할 수 있다.")
        @Test
        void saveMateWithDuplicateNickname() {
            String nickname = "제리";
            Member member1 = fixtureGenerator.generateMember(nickname);
            Member member2 = fixtureGenerator.generateMember(nickname);
            Meeting meeting = fixtureGenerator.generateMeeting();
            Mate mate1 = fixtureGenerator.generateMate(meeting, member1);

            MateSaveRequestV2 mateSaveRequest = new MateSaveRequestV2(
                    meeting.getInviteCode(),
                    Fixture.ORIGIN_LOCATION.getAddress(),
                    Fixture.ORIGIN_LOCATION.getLatitude(),
                    Fixture.ORIGIN_LOCATION.getLongitude()
            );
            MateSaveResponseV2 mateSaveResponse = mateService.saveAndSendNotifications(
                    mateSaveRequest,
                    member2,
                    meeting
            );

            assertThat(mateSaveResponse.meetingId()).isEqualTo(mate1.getId());
        }

        @DisplayName("하나의 약속에 동일한 회원이 존재할 수 없다.")
        @Test
        void saveMateWithDuplicateMember() {
            Member member = fixtureGenerator.generateMember("제리");
            Meeting meeting = fixtureGenerator.generateMeeting();
            fixtureGenerator.generateMate(meeting, member);

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
