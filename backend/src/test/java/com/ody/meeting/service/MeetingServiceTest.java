package com.ody.meeting.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;

import com.ody.common.BaseServiceTest;
import com.ody.common.DtoGenerator;
import com.ody.common.Fixture;
import com.ody.common.FixtureGenerator;
import com.ody.common.exception.OdyBadRequestException;
import com.ody.common.exception.OdyNotFoundException;
import com.ody.mate.domain.Mate;
import com.ody.mate.dto.request.MateSaveRequestV2;
import com.ody.mate.dto.response.MateResponse;
import com.ody.mate.repository.MateRepository;
import com.ody.meeting.domain.Meeting;
import com.ody.meeting.dto.request.MeetingSaveRequestV1;
import com.ody.meeting.dto.response.MeetingSaveResponseV1;
import com.ody.meeting.dto.response.MeetingWithMatesResponse;
import com.ody.meeting.repository.MeetingRepository;
import com.ody.member.domain.Member;
import com.ody.member.repository.MemberRepository;
import com.ody.notification.domain.message.GroupMessage;
import com.ody.util.TimeUtil;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.scheduling.TaskScheduler;

class MeetingServiceTest extends BaseServiceTest {

    @Autowired
    private MeetingService meetingService;

    @Autowired
    private FixtureGenerator fixtureGenerator;

    @MockBean
    private TaskScheduler taskScheduler;

    @DisplayName("내 약속 목록 조회 시 오름차순 정렬한다.")
    @Test
    void findAllByMember() {
        Member member = fixtureGenerator.generateMember();

        LocalDate today = LocalDate.now();
        LocalDateTime dayAfterTomorrowAt14 = LocalDateTime.of(today.plusDays(2), LocalTime.parse("14:00"));
        LocalDateTime tomorrowAt12 = LocalDateTime.of(today.plusDays(1), LocalTime.parse("12:00"));
        LocalDateTime tomorrowAt14 = LocalDateTime.of(today.plusDays(1), LocalTime.parse("14:00"));

        attendMultipleMeetingByTimes(member, dayAfterTomorrowAt14, tomorrowAt12, tomorrowAt14);

        List<LocalDateTime> foundMeetingTimes = meetingService.findAllByMember(member)
                .meetings()
                .stream()
                .map(response -> LocalDateTime.of(response.date(), response.time()))
                .toList();

        assertThat(foundMeetingTimes).containsExactly(tomorrowAt12, tomorrowAt14, dayAfterTomorrowAt14);
    }

    @DisplayName("내 약속 목록 조회 시 약속 시간이 현재 시간으로부터 24시간 포함 이내인 약속부터 미래의 약속까지만 조회된다.")
    @Test
    void findAllByMemberFilterTime() {
        Member member = fixtureGenerator.generateMember();

        LocalDateTime now24HoursAgo = TimeUtil.nowWithTrim().minusHours(24);
        LocalDateTime now24Hours1MinutesAgo = now24HoursAgo.minusMinutes(1);
        LocalDateTime now23Hours59MinutesAgo = now24HoursAgo.plusMinutes(1);

        attendMultipleMeetingByTimes(member, now24HoursAgo, now24Hours1MinutesAgo, now23Hours59MinutesAgo);

        List<LocalDateTime> foundMeetingTimes = meetingService.findAllByMember(member)
                .meetings()
                .stream()
                .map(response -> LocalDateTime.of(response.date(), response.time()))
                .toList();

        assertThat(foundMeetingTimes).containsExactly(now24HoursAgo, now23Hours59MinutesAgo);
    }

    private void attendMultipleMeetingByTimes(Member member, LocalDateTime... meetingTimes) {
        for (LocalDateTime meetingTime : meetingTimes) {
            Meeting meeting = fixtureGenerator.generateMeeting(meetingTime);
            fixtureGenerator.generateMate(meeting, member);
        }
    }

    @DisplayName("약속 저장에 성공한다")
    @Test
    void saveV1Success() {
        MeetingSaveRequestV1 request = dtoGenerator.generateMeetingRequest(Fixture.ODY_MEETING);

        MeetingSaveResponseV1 response = meetingService.saveV1(request);
        String generatedInviteCodeByRequest = request.toMeeting(response.inviteCode()).getInviteCode();

        assertAll(
                () -> assertThat(response.name()).isEqualTo(request.name()),
                () -> assertThat(response.date()).isEqualTo(request.date()),
                () -> assertThat(response.time()).isEqualTo(request.time()),
                () -> assertThat(response.targetAddress()).isEqualTo(request.targetAddress()),
                () -> assertThat(response.targetLatitude()).isEqualTo(request.targetLatitude()),
                () -> assertThat(response.targetLongitude()).isEqualTo(request.targetLongitude()),
                () -> assertThat(response.inviteCode()).isEqualTo(generatedInviteCodeByRequest)
        );
    }

    @DisplayName("약속 생성 후 약속 시간 30분 전에 ETA 공지 알림이 예약된다.")
    @Test
    void saveAndScheduleEtaNotice() {
        LocalDateTime meetingDateTime = TimeUtil.nowWithTrim().plusMinutes(40);
        MeetingSaveRequestV1 request = dtoGenerator.generateMeetingRequest(meetingDateTime);
        meetingService.saveV1(request);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        ArgumentCaptor<Instant> timeCaptor = ArgumentCaptor.forClass(Instant.class);

        Mockito.verify(taskScheduler).schedule(runnableCaptor.capture(), timeCaptor.capture());
        Instant scheduledTime = timeCaptor.getValue();
        runnableCaptor.getValue().run();

        assertAll(
                () -> assertThat(meetingDateTime.minusMinutes(30).toInstant(TimeUtil.KST_OFFSET))
                        .isEqualTo(scheduledTime),
                () -> Mockito.verify(fcmPushSender, Mockito.times(1)).sendNoticeMessage(any(GroupMessage.class))
        );
    }

    @DisplayName("약속과 참여자들 정보를 조회한다.")
    @Test
    void findMeetingWithMatesSuccess() {
        Member member1 = fixtureGenerator.generateMember();
        Member member2 = fixtureGenerator.generateMember();
        Meeting meeting = fixtureGenerator.generateMeeting();
        Mate mate1 = fixtureGenerator.generateMate(meeting, member1);
        Mate mate2 = fixtureGenerator.generateMate(meeting, member2);

        MeetingWithMatesResponse response = meetingService.findMeetingWithMates(member1, meeting.getId());

        List<String> mateNicknames = response.mates().stream()
                .map(MateResponse::nickname)
                .toList();

        assertAll(
                () -> assertThat(response.id()).isEqualTo(meeting.getId()),
                () -> assertThat(mateNicknames).containsOnly(
                        mate1.getNickname().getValue(),
                        mate2.getNickname().getValue()
                )
        );
    }

    @DisplayName("약속 조회 시, 약속이 존재하지 않으면 예외가 발생한다.")
    @Test
    void findMeetingWithMatesException() {
        Member member = fixtureGenerator.generateMember();

        assertThatThrownBy(() -> meetingService.findMeetingWithMates(member, 1L))
                .isInstanceOf(OdyNotFoundException.class);
    }

    @DisplayName("지나지 않은 약속에 참여가 가능하다")
    @Test
    void saveMateSuccess() {
        LocalDateTime oneMinutesLater = TimeUtil.nowWithTrim().plusMinutes(1L);
        Meeting notOverdueMeeting = fixtureGenerator.generateMeeting(oneMinutesLater);
        Member member = fixtureGenerator.generateMember();
        MateSaveRequestV2 mateSaveRequest = dtoGenerator.generateMateSaveRequest(notOverdueMeeting);

        assertThatCode(() -> meetingService.saveMateAndSendNotifications(mateSaveRequest, member))
                .doesNotThrowAnyException();
    }

    @DisplayName("지난 약속에 참여가 불가하다")
    @Test
    void saveMateFail_When_tryAttendOverdueMeeting() {
        LocalDateTime oneMinutesAgo = TimeUtil.nowWithTrim().minusMinutes(1L);
        Meeting overdueMeeting = fixtureGenerator.generateMeeting(oneMinutesAgo);
        Member member = fixtureGenerator.generateMember();
        MateSaveRequestV2 mateSaveRequest = dtoGenerator.generateMateSaveRequest(overdueMeeting);

        assertThatThrownBy(() -> meetingService.saveMateAndSendNotifications(mateSaveRequest, member))
                .isInstanceOf(OdyBadRequestException.class);
    }

    @DisplayName("초대코드를 가진 약속을 찾을 수 없으면 404를 반환한다")
    @Test
    void validateInvitedCodeFailWhenNotExistsInviteCode() {
        Member member = fixtureGenerator.generateMember();
        assertThatThrownBy(() -> meetingService.validateInviteCode(member, "none"))
                .isInstanceOf(OdyNotFoundException.class);
    }

    @DisplayName("이미 참여한 회원이 초대코드 검증을 요구하면 400을 반환한다")
    @Test
    void validateInviteCodeFailWhenAlreadyAttendedMeetingInviteCode() {
        Member member = fixtureGenerator.generateMember();
        Meeting meeting = fixtureGenerator.generateMeeting();
        Mate mate = fixtureGenerator.generateMate(meeting, member);

        assertThatThrownBy(() -> meetingService.validateInviteCode(member, meeting.getInviteCode()))
                .isInstanceOf(OdyBadRequestException.class);
    }
}
