package com.ody.meeting.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.times;

import com.ody.common.BaseServiceTest;
import com.ody.common.Fixture;
import com.ody.common.exception.OdyBadRequestException;
import com.ody.common.exception.OdyNotFoundException;
import com.ody.mate.domain.Mate;
import com.ody.mate.dto.request.MateSaveRequestV2;
import com.ody.mate.dto.response.MateResponse;
import com.ody.meeting.domain.Meeting;
import com.ody.meeting.dto.request.MeetingSaveRequestV1;
import com.ody.meeting.dto.response.MeetingSaveResponseV1;
import com.ody.meeting.dto.response.MeetingWithMatesResponseV1;
import com.ody.meeting.dto.response.MeetingWithMatesResponseV2;
import com.ody.meeting.repository.MeetingRepository;
import com.ody.member.domain.Member;
import com.ody.notification.domain.NotificationStatus;
import com.ody.notification.domain.NotificationType;
import com.ody.notification.service.FcmPushSender;
import com.ody.notification.service.event.NoticeEvent;
import com.ody.notification.service.event.UnSubscribeEvent;
import com.ody.route.domain.ClientType;
import com.ody.route.domain.DepartureTime;
import com.ody.route.service.ApiCallService;
import com.ody.util.TimeUtil;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronExpression;

class MeetingServiceTest extends BaseServiceTest {

    @Autowired
    private MeetingService meetingService;

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private ApiCallService apiCallService;

    @MockBean
    private TaskScheduler taskScheduler;

    @MockBean
    protected FcmPushSender fcmPushSender;

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

    @DisplayName("당일 약속 생성 후, 약속 시간 30분 전에 ETA 공지 알림과 ETA 스케줄링 알림이 예약된다.")
    @Test
    void saveAndScheduleEtaNoticeAndEtaSchedulingNotice() {
        LocalDateTime meetingDateTime = TimeUtil.nowWithTrim().plusHours(1);
        MeetingSaveRequestV1 request = dtoGenerator.generateMeetingRequest(meetingDateTime);
        meetingService.saveV1(request);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        ArgumentCaptor<Instant> timeCaptor = ArgumentCaptor.forClass(Instant.class);

        Mockito.verify(taskScheduler, times(2)).schedule(runnableCaptor.capture(), timeCaptor.capture());

        List<Instant> scheduledTimes = timeCaptor.getAllValues();
        runnableCaptor.getAllValues().forEach(Runnable::run);

        assertAll(
                () -> assertThat(scheduledTimes).containsOnly(
                        meetingDateTime.minusMinutes(30).toInstant(TimeUtil.KST_OFFSET)),
                () -> assertThat(applicationEvents.stream(NoticeEvent.class)).hasSize(2)
        );
    }

    @DisplayName("내일 오전 5시 이내 약속을 생성한 경우가 아니면, 약속 시간 30분 전에 ETA 공지 알림만 예약된다.")
    @Test
    void saveAndScheduleEtaNotice() {
        LocalDateTime twoDaysLater = TimeUtil.nowWithTrim().plusDays(2);
        MeetingSaveRequestV1 request = dtoGenerator.generateMeetingRequest(twoDaysLater);
        meetingService.saveV1(request);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        ArgumentCaptor<Instant> timeCaptor = ArgumentCaptor.forClass(Instant.class);

        Mockito.verify(taskScheduler).schedule(runnableCaptor.capture(), timeCaptor.capture());
        Instant scheduledTime = timeCaptor.getValue();
        runnableCaptor.getValue().run();

        assertAll(
                () -> assertThat(twoDaysLater.minusMinutes(30).toInstant(TimeUtil.KST_OFFSET))
                        .isEqualTo(scheduledTime),
                () -> assertThat(applicationEvents.stream(NoticeEvent.class)).hasSize(1)
        );
    }

    @DisplayName("약속 조회 성공 V1: 약속과 참여자들 정보를 조회한다.")
    @Test
    void findMeetingWithMatesV1Success() {
        Member member1 = fixtureGenerator.generateMember();
        Member member2 = fixtureGenerator.generateMember();
        Meeting meeting = fixtureGenerator.generateMeeting();
        Mate mate1 = fixtureGenerator.generateMate(meeting, member1);
        Mate mate2 = fixtureGenerator.generateMate(meeting, member2);

        MeetingWithMatesResponseV1 response = meetingService.findMeetingWithMatesV1(member1, meeting.getId());

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

    @DisplayName("약속 조회 성공 V2: 약속과 참여자들 정보를 조회한다.")
    @Test
    void findMeetingWithMatesV2Success() {
        Member member1 = fixtureGenerator.generateMember();
        Member member2 = fixtureGenerator.generateMember();
        Meeting meeting = fixtureGenerator.generateMeeting();
        Mate mate1 = fixtureGenerator.generateMate(meeting, member1);
        Mate mate2 = fixtureGenerator.generateMate(meeting, member2);
        DepartureTime mateDepartureTime = new DepartureTime(meeting, mate1.getEstimatedMinutes());
        LocalTime expectedDepartureTime = mateDepartureTime.getValue().toLocalTime();

        MeetingWithMatesResponseV2 response = meetingService.findMeetingWithMatesV2(member1, meeting.getId());

        List<String> mateNicknames = response.mates().stream()
                .map(MateResponse::nickname)
                .toList();

        assertAll(
                () -> assertThat(response.id()).isEqualTo(meeting.getId()),
                () -> assertThat(response.routeTime()).isEqualTo(mate1.getEstimatedMinutes()),
                () -> assertThat(response.departureTime()).isEqualTo(expectedDepartureTime),
                () -> assertThat(response.originAddress()).isEqualTo(mate1.getOriginAddress()),
                () -> assertThat(mateNicknames).containsOnly(
                        mate1.getNickname().getValue(),
                        mate2.getNickname().getValue()
                )
        );
    }

    @DisplayName("약속 조회 실패 V1: 약속 조회 시, 약속이 존재하지 않으면 예외가 발생한다.")
    @Test
    void findMeetingWithMatesV1Exception() {
        Member member = fixtureGenerator.generateMember();

        assertThatThrownBy(() -> meetingService.findMeetingWithMatesV1(member, 1L))
                .isInstanceOf(OdyNotFoundException.class);
    }

    @DisplayName("약속 조회 실패 V2: 약속 조회 시, 약속이 존재하지 않으면 예외가 발생한다.")
    @Test
    void findMeetingWithMatesV2Exception() {
        Member member = fixtureGenerator.generateMember();

        assertThatThrownBy(() -> meetingService.findMeetingWithMatesV2(member, 1L))
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
        fixtureGenerator.generateMate(meeting, member);

        assertThatThrownBy(() -> meetingService.validateInviteCode(member, meeting.getInviteCode()))
                .isInstanceOf(OdyBadRequestException.class);
    }

    @DisplayName("오전 4시 마다 약속 시간이 지난 약속들의 상태를 기간 지남으로 변경하고 구독한 topic을 취소한다.")
    @Test
    void scheduleOverdueMeetings() {
        CronExpression expression = CronExpression.parse("0 0 4 * * *");
        LocalDateTime dateTime = LocalDateTime.of(2024, 10, 10, 3, 59, 59);
        LocalDateTime nextExecutionTime = expression.next(dateTime);

        assertAll(
                () -> assertThat(LocalDateTime.of(2024, 10, 10, 3, 59, 59)).isNotEqualTo(nextExecutionTime),
                () -> assertThat(LocalDateTime.of(2024, 10, 10, 4, 0, 0)).isEqualTo(nextExecutionTime),
                () -> assertThat(LocalDateTime.of(2024, 10, 10, 4, 0, 1)).isNotEqualTo(nextExecutionTime),
                () -> assertThat(LocalDateTime.of(2024, 10, 11, 4, 0, 0)).isEqualTo(expression.next(nextExecutionTime))
        );

        Meeting meeting = fixtureGenerator.generateMeeting(dateTime);
        Mate mate = fixtureGenerator.generateMate(meeting);
        fixtureGenerator.generateNotification(mate, NotificationType.DEPARTURE_REMINDER, NotificationStatus.DONE);
        meetingService.scheduleOverdueMeetings();

        Meeting findMeeting = meetingRepository.findById(meeting.getId()).get();

        assertAll(
                () -> assertThat(applicationEvents.stream(UnSubscribeEvent.class)).hasSize(1),
                () -> assertThat(findMeeting.isOverdue()).isTrue()
        );
    }

    @DisplayName("약속 참여시 API 호출 카운팅 동시성 테스트")
    @Nested
    class ApiCallCountConcurrencyTest {

        private static final int TOTAL_REQUESTS = 100;

        @DisplayName("100명의 사용자가 동시에 약속에 참여하여 API를 호출할 경우 정확히 count+100 한다.")
        @Test
        void concurrencySaveMateAndSendNotifications() throws InterruptedException {
            ExecutorService executorService = Executors.newFixedThreadPool(TOTAL_REQUESTS);
            CountDownLatch countDownLatch = new CountDownLatch(TOTAL_REQUESTS);

            Meeting meeting = fixtureGenerator.generateMeeting(LocalDateTime.now());
            for (int i = 1; i <= TOTAL_REQUESTS; i++) {
                executorService.execute(() -> {
                    try {
                        Member member = fixtureGenerator.generateMember();
                        MateSaveRequestV2 mateSaveRequest = dtoGenerator.generateMateSaveRequest(meeting);
                        meetingService.saveMateAndSendNotifications(mateSaveRequest, member);
                    } finally {
                        countDownLatch.countDown();
                    }
                });
            }
            countDownLatch.await(3, TimeUnit.SECONDS);
            executorService.shutdown();
            executorService.awaitTermination(3, TimeUnit.SECONDS);

            int actual = apiCallService.countApiCall(ClientType.ODSAY).count();

            assertThat(actual).isEqualTo(TOTAL_REQUESTS);
        }

        @DisplayName("100명의 사용자가 동시에 약속에 참여하여 절반이 예외가 발생하면 해당 트랜잭션은 롤백되어 count+50 한다.")
        @Test
        void concurrencySaveMateAndSendNotificationsRollBack() throws InterruptedException {
            ExecutorService executorService = Executors.newFixedThreadPool(TOTAL_REQUESTS);
            CountDownLatch countDownLatch = new CountDownLatch(TOTAL_REQUESTS);

            Meeting meeting = fixtureGenerator.generateMeeting(LocalDateTime.now().plusHours(1));

            for (int i = 1; i <= TOTAL_REQUESTS; i++) {
                final int index = i;
                executorService.execute(() -> {
                    try {
                        Member member = fixtureGenerator.generateMember();
                        MateSaveRequestV2 mateSaveRequest = dtoGenerator.generateMateSaveRequest(meeting);
                        if (index % 2 == 0) {
                            throw new RuntimeException();
                        }
                        meetingService.saveMateAndSendNotifications(mateSaveRequest, member);
                    } finally {
                        countDownLatch.countDown();
                    }
                });
            }
            countDownLatch.await(3, TimeUnit.SECONDS);
            executorService.shutdown();
            executorService.awaitTermination(3, TimeUnit.SECONDS);

            int actual = apiCallService.countApiCall(ClientType.ODSAY).count();

            assertThat(actual).isEqualTo(TOTAL_REQUESTS / 2);
        }
    }
}
