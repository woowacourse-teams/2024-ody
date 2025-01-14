package com.ody.eta.service;

import static com.ody.common.Fixture.TARGET_LOCATION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.ody.common.BaseServiceTest;
import com.ody.meeting.domain.Meeting;
import com.ody.meeting.repository.MeetingRepository;
import com.ody.notification.service.event.NoticeEvent;
import com.ody.util.InviteCodeGenerator;
import com.ody.util.TimeUtil;
import java.time.Instant;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.scheduling.TaskScheduler;

class EtaSchedulingServiceTest extends BaseServiceTest {

    @Autowired
    private EtaSchedulingService etaSchedulingService;

    @Autowired
    private MeetingRepository meetingRepository;

    @MockBean
    private TaskScheduler taskScheduler;

    @DisplayName("약속 시간 30분 전 ETA 스케줄링 알림이 예약된다.")
    @Test
    void scheduleEtaSchedulingNotice() {
        LocalDateTime oneHourLater = TimeUtil.nowWithTrim().plusHours(1L);
        Meeting oneHourLaterMeeting = new Meeting(
                "우테코 등교",
                oneHourLater.toLocalDate(),
                oneHourLater.toLocalTime(),
                TARGET_LOCATION,
                InviteCodeGenerator.generate()
        );
        Meeting meeting = meetingRepository.save(oneHourLaterMeeting);

        etaSchedulingService.scheduleEtaSchedulingNotice(meeting);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        ArgumentCaptor<Instant> timeCaptor = ArgumentCaptor.forClass(Instant.class);

        Mockito.verify(taskScheduler).schedule(runnableCaptor.capture(), timeCaptor.capture());
        Instant scheduledTime = timeCaptor.getValue();
        runnableCaptor.getValue().run();

        assertAll(
                () -> assertThat(oneHourLater.minusMinutes(30).toInstant(TimeUtil.KST_OFFSET))
                        .isEqualTo(scheduledTime),
                () -> assertThat(applicationEvents.stream(NoticeEvent.class)).hasSize(1)
        );
    }

    @DisplayName("ETA 스케줄링 알림 발송 전 약속이 삭제되면 알림이 발송되지 않는다.")
    @Test
    void doesNotSendEtaSchedulingNoticeIfInvalidMeeting() {
        LocalDateTime oneHourLater = TimeUtil.nowWithTrim().plusHours(1L);
        Meeting oneHourLaterMeeting = new Meeting(
                "우테코 등교",
                oneHourLater.toLocalDate(),
                oneHourLater.toLocalTime(),
                TARGET_LOCATION,
                InviteCodeGenerator.generate()
        );
        Meeting meeting = meetingRepository.save(oneHourLaterMeeting);

        etaSchedulingService.scheduleEtaSchedulingNotice(meeting);

        meetingRepository.delete(meeting); // 현재 meeting 삭제 기능은 없어서 검증할 필요 없긴 함

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        ArgumentCaptor<Instant> timeCaptor = ArgumentCaptor.forClass(Instant.class);

        Mockito.verify(taskScheduler).schedule(runnableCaptor.capture(), timeCaptor.capture());
        Instant scheduledTime = timeCaptor.getValue();
        runnableCaptor.getValue().run();

        assertAll(
                () -> assertThat(oneHourLater.minusMinutes(30).toInstant(TimeUtil.KST_OFFSET))
                        .isEqualTo(scheduledTime),
                () -> assertThat(applicationEvents.stream(NoticeEvent.class)).isEmpty()
        );
    }
}
