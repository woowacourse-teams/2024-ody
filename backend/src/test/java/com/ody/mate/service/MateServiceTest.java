package com.ody.mate.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.ody.common.BaseServiceTest;
import com.ody.common.Fixture;
import com.ody.common.exception.OdyBadRequestException;
import com.ody.common.exception.OdyNotFoundException;
import com.ody.eta.domain.Eta;
import com.ody.mate.domain.Mate;
import com.ody.mate.dto.request.MateSaveRequestV2;
import com.ody.mate.dto.request.NudgeRequest;
import com.ody.mate.dto.response.MateSaveResponseV2;
import com.ody.meeting.domain.Meeting;
import com.ody.meetinglog.domain.MeetingLog;
import com.ody.meetinglog.domain.MeetingLogType;
import com.ody.meetinglog.repository.MeetingLogRepository;
import com.ody.member.domain.DeviceToken;
import com.ody.member.domain.Member;
import com.ody.notification.domain.FcmTopic;
import com.ody.notification.service.event.NudgeEvent;
import com.ody.notification.service.event.UnSubscribeEvent;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MateServiceTest extends BaseServiceTest {

    @Autowired
    private MateService mateService;

    @Autowired
    private MeetingLogRepository meetingLogRepository;

    @DisplayName("회원이 참여하고 있는 특정 약속의 참여자 리스트를 조회한다.")
    @Test
    void findAllByMemberAndMeetingIdSuccess() {
        Meeting meeting = fixtureGenerator.generateMeeting();
        Member member1 = fixtureGenerator.generateMember();
        Mate mate1 = fixtureGenerator.generateMate(meeting, member1);
        Mate mate2 = fixtureGenerator.generateMate(meeting);

        List<Mate> mates = mateService.findAllByMeetingIdIfMate(member1, meeting.getId());
        List<Long> mateIds = mates.stream()
                .map(Mate::getId)
                .toList();

        assertThat(mateIds).containsOnly(mate1.getId(), mate2.getId());
    }

    @DisplayName("약속에 참여하고 있는 회원이 아니면 예외가 발생한다.")
    @Test
    void findAllByMemberAndMeetingIdException() {
        Member member1 = fixtureGenerator.generateMember();
        Member member2 = fixtureGenerator.generateMember();
        Meeting meeting = fixtureGenerator.generateMeeting();
        Mate mate = fixtureGenerator.generateMate(meeting, member1);

        assertThatThrownBy(() -> mateService.findAllByMeetingIdIfMate(member2, meeting.getId()))
                .isInstanceOf(OdyNotFoundException.class);
    }

    @DisplayName("재촉하기 테스트")
    @Nested
    class NudgeTest {

        @DisplayName("약속 재촉에 성공하면 재촉 로그가 남는다")
        @Test
        void saveNudgeLogWhenNudgeSuccess() {
            Meeting oneMinuteLaterMeeting = fixtureGenerator.generateMeeting(LocalDateTime.now().plusMinutes(1L));
            Mate requestMate = fixtureGenerator.generateMate(oneMinuteLaterMeeting);
            Mate nudgedLateWarningMate = fixtureGenerator.generateMate(oneMinuteLaterMeeting);
            Eta lateWarningEta = fixtureGenerator.generateEta(nudgedLateWarningMate, 2L);

            NudgeRequest nudgeRequest = new NudgeRequest(requestMate.getId(), nudgedLateWarningMate.getId());
            mateService.nudge(nudgeRequest);

            List<MeetingLog> meetingLogs = meetingLogRepository.findByShowAtBeforeOrEqualTo(
                    oneMinuteLaterMeeting.getId(), LocalDateTime.now()
            );

            assertAll(
                    () -> assertThat(meetingLogs).hasSize(1),
                    () -> assertThat(meetingLogs.get(0).getType()).isEqualTo(MeetingLogType.NUDGE_LOG)
            );
        }

        @DisplayName("약속이 1분 뒤이고 소요시간이 2분으로 Eta상태가 지각 위기인 mate를 재촉할 수 있다")
        @Test
        void nudgeSuccessWhenLateWarning() {
            Meeting oneMinuteLaterMeeting = fixtureGenerator.generateMeeting(LocalDateTime.now().plusMinutes(1L));
            Mate requestMate = fixtureGenerator.generateMate(oneMinuteLaterMeeting);
            Mate nudgedLateWarningMate = fixtureGenerator.generateMate(oneMinuteLaterMeeting);
            Eta lateWarningEta = fixtureGenerator.generateEta(nudgedLateWarningMate, 2L);

            NudgeRequest nudgeRequest = new NudgeRequest(requestMate.getId(), nudgedLateWarningMate.getId());
            mateService.nudge(nudgeRequest);

            assertThat(applicationEvents.stream(NudgeEvent.class))
                    .hasSize(1);
        }

        @DisplayName("약속이 지금이고 소요시간이 2분으로 Eta상태가 지각인 mate를 재촉할 수 있다")
        @Test
        void nudgeSuccessWhenLate() {
            Meeting nowMeeting = fixtureGenerator.generateMeeting(LocalDateTime.now());
            Mate requestMate = fixtureGenerator.generateMate(nowMeeting);
            Mate nudgedLateMate = fixtureGenerator.generateMate(nowMeeting);
            Eta lateEta = fixtureGenerator.generateEta(nudgedLateMate, 2L);

            NudgeRequest nudgeRequest = new NudgeRequest(requestMate.getId(), nudgedLateMate.getId());
            mateService.nudge(nudgeRequest);

            assertThat(applicationEvents.stream(NudgeEvent.class))
                    .hasSize(1);
        }

        @DisplayName("같은 약속 참여자가 아니라면 재촉할 수 없다")
        @Test
        void nudgedFailedWhenDifferentMeetingAttender() {
            Meeting meeting1 = fixtureGenerator.generateMeeting(LocalDateTime.now().plusMinutes(1L));
            Meeting meeting2 = fixtureGenerator.generateMeeting(LocalDateTime.now().plusMinutes(1L));
            Mate requestMate = fixtureGenerator.generateMate(meeting1);
            Mate nudgedMate = fixtureGenerator.generateMate(meeting2);

            NudgeRequest nudgeRequest = new NudgeRequest(requestMate.getId(), nudgedMate.getId());

            assertThatThrownBy(() -> mateService.nudge(nudgeRequest))
                    .isInstanceOf(OdyBadRequestException.class);
        }

        @DisplayName("약속이 3분 뒤이고 소요시간이 2분으로 Eta상태가 도착 예정인 mate를 재촉하면 예외가 발생한다")
        @Test
        void nudgeFailWhenArriavalSoon() {
            Meeting threeMinutesLaterMeeting = fixtureGenerator.generateMeeting(LocalDateTime.now().plusMinutes(3L));
            Mate requestMate = fixtureGenerator.generateMate(threeMinutesLaterMeeting);
            Mate nudgedArriavalSoonMate = fixtureGenerator.generateMate(threeMinutesLaterMeeting);

            NudgeRequest nudgeRequest = new NudgeRequest(requestMate.getId(), nudgedArriavalSoonMate.getId());
            Eta arrivalSoonEta = fixtureGenerator.generateEta(nudgedArriavalSoonMate, 2L);

            assertThatThrownBy(() -> mateService.nudge(nudgeRequest))
                    .isInstanceOf(OdyBadRequestException.class);
        }

        @DisplayName("3분 뒤 약속에 약속장소에 도착하여 Eta상태가 도착인 mate를 재촉하면 예외가 발생한다")
        @Test
        void nudgeFailWhenArrived() {
            Meeting threeMinutesLaterMeeting = fixtureGenerator.generateMeeting(LocalDateTime.now().plusMinutes(3L));
            Mate requestMate = fixtureGenerator.generateMate(threeMinutesLaterMeeting);
            Mate nudgedArrivedMate = fixtureGenerator.generateMate(threeMinutesLaterMeeting, Fixture.TARGET_LOCATION);
            Eta arrivedEta = fixtureGenerator.generateEta(nudgedArrivedMate, 0L);

            NudgeRequest nudgeRequest = new NudgeRequest(requestMate.getId(), nudgedArrivedMate.getId());

            assertThatThrownBy(() -> mateService.nudge(nudgeRequest))
                    .isInstanceOf(OdyBadRequestException.class);
        }

        @DisplayName("약속 30분 이후까지 mate를 재촉할 수 있다")
        @Test
        void nudgeSuccessWhenTimeWithInNudgeAvailableTime() {
            Meeting availableNudgeMeeting = fixtureGenerator.generateMeeting(LocalDateTime.now().minusMinutes(30L));
            Mate requestMate = fixtureGenerator.generateMate(availableNudgeMeeting);
            Mate nudgedLateWarningMate = fixtureGenerator.generateMate(availableNudgeMeeting);
            Eta lateWarningEta = fixtureGenerator.generateEta(nudgedLateWarningMate, 2L);

            NudgeRequest nudgeRequest = new NudgeRequest(requestMate.getId(), nudgedLateWarningMate.getId());
            mateService.nudge(nudgeRequest);

            assertThat(applicationEvents.stream(NudgeEvent.class))
                    .hasSize(1);
        }

        @DisplayName("약속 30분 이후에는 mate를 재촉할 수 없다")
        @Test
        void nudgeFailWhenTimeIsNotWithInNudgeAvailableTime() {
            Meeting notAvailableNudgeMeeting = fixtureGenerator.generateMeeting(LocalDateTime.now().minusMinutes(31L));
            Mate requestMate = fixtureGenerator.generateMate(notAvailableNudgeMeeting);
            Mate nudgedLateWarningMate = fixtureGenerator.generateMate(notAvailableNudgeMeeting);
            Eta lateWarningEta = fixtureGenerator.generateEta(nudgedLateWarningMate, 2L);

            NudgeRequest nudgeRequest = new NudgeRequest(requestMate.getId(), nudgedLateWarningMate.getId());

            assertAll(
                    () -> assertThatThrownBy(() -> mateService.nudge(nudgeRequest))
                            .isInstanceOf(OdyBadRequestException.class),
                    () -> assertThat(applicationEvents.stream(NudgeEvent.class))
                            .isEmpty()
            );
        }
    }

    @DisplayName("참여자 생성")
    @Nested
    class saveAndSendNotifications {

        @DisplayName("나중에 출발해야 할 때 : 입장 로그가 남는다")
        @Test
        void saveEntryLogWhenLateDeparture() {
            Member member = fixtureGenerator.generateMember();
            Meeting laterMeeting = fixtureGenerator.generateMeeting(LocalDateTime.now().plusHours(1L));

            MateSaveRequestV2 mateSaveRequest = dtoGenerator.generateMateSaveRequest(laterMeeting);
            mateService.saveAndSendNotifications(mateSaveRequest, member, laterMeeting);

            List<MeetingLog> meetingLogs = meetingLogRepository.findByShowAtBeforeOrEqualTo(
                    laterMeeting.getId(), LocalDateTime.now()
            );

            assertAll(
                    () -> assertThat(meetingLogs).hasSize(1),
                    () -> assertThat(meetingLogs.get(0).getType()).isEqualTo(MeetingLogType.ENTRY_LOG)
            );
        }

        @DisplayName("당장 출발해야 할 때 : 입장 로그 + 출발 알림 로그가 남는다")
        @Test
        void saveEntryAndDepartureReminderLogWhenNowDeparture() throws InterruptedException {
            Member member = fixtureGenerator.generateMember();
            Meeting now = fixtureGenerator.generateMeeting(LocalDateTime.now());
            MateSaveRequestV2 mateSaveRequest = dtoGenerator.generateMateSaveRequest(now);

            mateService.saveAndSendNotifications(mateSaveRequest, member, now);

            List<MeetingLogType> meetingLogTypes = meetingLogRepository.findByShowAtBeforeOrEqualTo(now.getId(), LocalDateTime.now())
                    .stream()
                    .map(MeetingLog::getType)
                    .toList();

            assertAll(
                    () -> assertThat(meetingLogTypes).hasSize(2),
                    () -> assertThat(meetingLogTypes).containsExactly(MeetingLogType.ENTRY_LOG, MeetingLogType.DEPARTURE_REMINDER)
            );
        }

        @DisplayName("하나의 약속에 동일한 닉네임을 가진 참여자가 존재할 수 있다.")
        @Test
        void saveMateWithDuplicateNickname() {
            Member member1 = fixtureGenerator.generateMember();
            Member member2 = fixtureGenerator.generateMember();
            Meeting meeting = fixtureGenerator.generateMeeting();
            Mate mate1 = fixtureGenerator.generateMate(meeting, member1);

            MateSaveRequestV2 mateSaveRequest = dtoGenerator.generateMateSaveRequest(meeting);

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
            Member member = fixtureGenerator.generateMember();
            Meeting meeting = fixtureGenerator.generateMeeting();
            fixtureGenerator.generateMate(meeting, member);

            MateSaveRequestV2 mateSaveRequest = dtoGenerator.generateMateSaveRequest(meeting);

            assertThatThrownBy(() -> mateService.saveAndSendNotifications(mateSaveRequest, member, meeting))
                    .isInstanceOf(OdyBadRequestException.class);
        }
    }

    @DisplayName("참여자 삭제 시, 삭제 로그가 남는다")
    @Test
    void saveMemberDeleteLogWhenDeleteMate() {
        Meeting meeting = fixtureGenerator.generateMeeting();
        Mate mate = fixtureGenerator.generateMate(meeting);
        FcmTopic fcmTopic = new FcmTopic(meeting);
        DeviceToken deviceToken = mate.getMember().getDeviceToken();

        mateService.withdraw(mate);

        List<MeetingLog> meetingLogs = meetingLogRepository.findByShowAtBeforeOrEqualTo(meeting.getId(), LocalDateTime.now());

        assertAll(
                () -> assertThat(meetingLogs).hasSize(1),
                () -> assertThat(meetingLogs.get(0).getType()).isEqualTo(MeetingLogType.MEMBER_DELETION_LOG)
        );
    }

    @DisplayName("참여자 삭제 시, 구독하고 있는 fcmTopic 취소힌다.")
    @Test
    void unSubscribeTopicWhenDeleteMate() {
        Mate mate = fixtureGenerator.generateMate();
        FcmTopic fcmTopic = new FcmTopic(mate.getMeeting());
        DeviceToken deviceToken = mate.getMember().getDeviceToken();

        mateService.withdraw(mate);

        assertThat(applicationEvents.stream(UnSubscribeEvent.class))
                .hasSize(1)
                .anySatisfy(event -> {
                            assertAll(
                                    () -> assertThat(event.getTopic()).isEqualTo(fcmTopic),
                                    () -> assertThat(event.getDeviceToken()).isEqualTo(deviceToken)
                            );
                        }
                );
    }

    @DisplayName("회원 삭제 시, 구독하고 있는 모든 fcmTopic을 취소한다.")
    @Test
    void unSubscribeAllTopicsWhenDeleteMember() {
        Member jojo = fixtureGenerator.generateMember("jojo");
        Member olive = fixtureGenerator.generateMember("olive");

        fixtureGenerator.generateMate(jojo);
        fixtureGenerator.generateMate(jojo);
        fixtureGenerator.generateMate(olive);

        mateService.deleteAllByMember(jojo);

        assertThat(applicationEvents.stream(UnSubscribeEvent.class))
                .hasSize(2);
    }

    @DisplayName("약속에 참여하고 있는 mate를 약속 방에서 삭제한다.")
    @Test
    void deleteMateByMeetingIdAndMemberId() {
        Meeting meeting = fixtureGenerator.generateMeeting();
        Member kaki = fixtureGenerator.generateMember("kaki");
        fixtureGenerator.generateMate(meeting, kaki);

        mateService.leaveByMeetingIdAndMemberId(meeting.getId(), kaki.getId());

        assertThatThrownBy(() -> mateService.findAllByMeetingIdIfMate(kaki, meeting.getId()))
                .isInstanceOf(OdyNotFoundException.class);
    }

    @DisplayName("방을 나갔다면 방 나가기 로그가 남는다")
    @Test
    void saveLeaveLogWhenMateLeave() {
        Meeting meeting = fixtureGenerator.generateMeeting();
        Member kaki = fixtureGenerator.generateMember("kaki");
        fixtureGenerator.generateMate(meeting, kaki);

        mateService.leaveByMeetingIdAndMemberId(meeting.getId(), kaki.getId());

        List<MeetingLog> meetingLogs = meetingLogRepository.findByShowAtBeforeOrEqualTo(meeting.getId(), LocalDateTime.now());

        assertAll(
                () -> assertThat(meetingLogs).hasSize(1),
                () -> assertThat(meetingLogs.get(0).getType()).isEqualTo(MeetingLogType.LEAVE_LOG)
        );
    }
}
