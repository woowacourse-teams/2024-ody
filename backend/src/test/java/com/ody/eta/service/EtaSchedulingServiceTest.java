package com.ody.eta.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.ody.common.BaseServiceTest;
import com.ody.eta.domain.EtaSchedulingKey;
import com.ody.mate.domain.Mate;
import com.ody.meeting.domain.Meeting;
import com.ody.meeting.repository.MeetingRepository;
import com.ody.notification.domain.message.DirectMessage;
import com.ody.notification.domain.message.GroupMessage;
import com.ody.notification.domain.notice.EtaSchedulingNotice;
import com.ody.notification.service.NoticeService;
import com.ody.notification.service.event.NoticeEvent;
import com.ody.util.TimeUtil;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.scheduling.TaskScheduler;

class EtaSchedulingServiceTest extends BaseServiceTest {

    @Autowired
    private EtaSchedulingService etaSchedulingService;

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private EtaService etaService;

    @SpyBean
    private NoticeService noticeService;

    @MockBean
    private TaskScheduler taskScheduler;

    @Value("${spring.data.redis.ttl}")
    private int ttlMs;

    @DisplayName("약속 30분 전 ETA 스케줄링 알림")
    @Nested
    class Notice30MinutesBeforeMeeting {

        @DisplayName("약속 시간 30분 전 ETA 스케줄링 알림이 예약된다.")
        @Test
        void scheduleNoticeByGroupMessage() {
            LocalDateTime oneHourLater = TimeUtil.nowWithTrim().plusHours(1L);
            Meeting meeting = fixtureGenerator.generateMeeting(oneHourLater);

            etaSchedulingService.sendNotice(meeting);

            ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
            ArgumentCaptor<Instant> timeCaptor = ArgumentCaptor.forClass(Instant.class);

            verify(taskScheduler).schedule(runnableCaptor.capture(), timeCaptor.capture());
            Instant scheduledTime = timeCaptor.getValue();
            runnableCaptor.getValue().run();

            assertAll(
                    () -> assertThat(oneHourLater.minusMinutes(30).toInstant(TimeUtil.KST_OFFSET))
                            .isEqualTo(scheduledTime),
                    () -> assertThat(applicationEvents.stream(NoticeEvent.class)).hasSize(1)
            );
        }

        @DisplayName("ETA 스케줄링 알림 전 약속이 삭제되면 알림이 발송되지 않는다.")
        @Test
        void doesNotSendNoticeByGroupMessageIfInvalidMeeting() {
            LocalDateTime oneHourLater = TimeUtil.nowWithTrim().plusHours(1L);
            Meeting meeting = fixtureGenerator.generateMeeting(oneHourLater);

            etaSchedulingService.sendNotice(meeting);

            meetingRepository.delete(meeting);

            ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
            ArgumentCaptor<Instant> timeCaptor = ArgumentCaptor.forClass(Instant.class);

            verify(taskScheduler).schedule(runnableCaptor.capture(), timeCaptor.capture());
            Instant scheduledTime = timeCaptor.getValue();
            runnableCaptor.getValue().run();

            assertAll(
                    () -> assertThat(oneHourLater.minusMinutes(30).toInstant(TimeUtil.KST_OFFSET))
                            .isEqualTo(scheduledTime),
                    () -> assertThat(applicationEvents.stream(NoticeEvent.class)).isEmpty()
            );
        }

        @DisplayName("ETA 스케줄링 알림 발송 시간이 이미 지난 경우 즉시 보낸다.")
        @Test
        void sendNoticeByGroupMessage() {
            LocalDateTime thirtyMinutesLater = TimeUtil.nowWithTrim().plusMinutes(30);
            Meeting meeting = fixtureGenerator.generateMeeting(thirtyMinutesLater);

            etaSchedulingService.sendNotice(meeting);

            verify(noticeService).send(any(EtaSchedulingNotice.class), any(GroupMessage.class));
        }
    }

    @DisplayName("ETA 스케줄링 알림 재시도")
    @Nested
    class RetryEtaSchedulingNotice {

        @DisplayName("다이렉트 메시지로 ETA 스케줄링 알림을 발송한다.")
        @Test
        void sendNoticeByDirectMessage() {
            LocalDateTime oneHourLater = TimeUtil.nowWithTrim().plusHours(1L);
            Meeting meeting = fixtureGenerator.generateMeeting(oneHourLater);
            Mate mate = fixtureGenerator.generateMate(meeting);

            EtaSchedulingKey etaSchedulingKey = EtaSchedulingKey.from(mate);

            etaSchedulingService.sendFallbackNotice(etaSchedulingKey);

            verify(noticeService).send(any(EtaSchedulingNotice.class), any(DirectMessage.class));
        }

        @DisplayName("이미 지난 약속이면 ETA 스케줄링 알림을 발송하지 않는다.")
        @Test
        void doesNotSendNoticeByDirectMessageIfOverdue() {
            LocalDateTime thirtyMinutesAgo = TimeUtil.nowWithTrim().minusMinutes(30L);
            Meeting meeting = fixtureGenerator.generateMeeting(thirtyMinutesAgo);
            Mate mate = fixtureGenerator.generateMate(meeting);

            EtaSchedulingKey etaSchedulingKey = EtaSchedulingKey.from(mate);

            etaSchedulingService.sendFallbackNotice(etaSchedulingKey);

            verify(noticeService, never()).send(any(EtaSchedulingNotice.class), any(DirectMessage.class));
        }
    }

    @DisplayName("ETA 스케줄링 알림 시나리오")
    @Nested
    class EtaSchedulingNoticeScenario {

        @DisplayName("정상 시나리오 : 스케줄링 그룹 알림 전송 -> ETA 요청 -> 스케줄링 개인 알림 재전송 X")
        @TestFactory
        Stream<DynamicTest> normalCase() {
            LocalDateTime now = TimeUtil.nowWithTrim();
            Meeting meeting = fixtureGenerator.generateMeeting(now.plusMinutes(30));
            Mate mate = fixtureGenerator.generateMate(meeting);
            fixtureGenerator.generateEta(mate);

            return Stream.of(
                    dynamicTest("참가자들에게 ETA 스케줄링 알림을 전송한다.", () -> {
                        etaSchedulingService.sendNotice(meeting);
                        verify(noticeService).send(any(EtaSchedulingNotice.class), any(GroupMessage.class));
                    }),
                    dynamicTest("TTL 만료 전에 ETA api 요청이 오면, 스케줄링 알림이 재전송되지 않는다", () -> {
                        Thread.sleep(ttlMs - 200);
                        etaService.findAllMateEtas(dtoGenerator.generateMateEtaRequest(), mate);
                        Thread.sleep(ttlMs - 200);
                        verify(noticeService, never()).send(any(EtaSchedulingNotice.class), any(DirectMessage.class));
                    })
            );
        }

        @DisplayName("재발송 시나리오 : 스케줄링 그룹 알림 전송 -> ETA 요청 X -> 스케줄링 개인 알림 재전송")
        @TestFactory
        Stream<DynamicTest> resendCase() {
            LocalDateTime now = TimeUtil.nowWithTrim();
            Meeting meeting = fixtureGenerator.generateMeeting(now.plusMinutes(30));
            Mate mate = fixtureGenerator.generateMate(meeting);
            fixtureGenerator.generateEta(mate);

            return Stream.of(
                    dynamicTest("참자가들에게 ETA 스케줄링 알림을 전송한다.", () -> {
                        etaSchedulingService.sendNotice(meeting);
                        verify(noticeService).send(any(EtaSchedulingNotice.class), any(GroupMessage.class));
                    }),
                    dynamicTest("TTL 만료 전에 ETA api 요청이 안 오면, 스케줄링 알림이 재전송된다.", () -> {
                        Thread.sleep(ttlMs + 500);
                        verify(noticeService).send(any(EtaSchedulingNotice.class), any(DirectMessage.class));
                    })
            );
        }
    }
}
