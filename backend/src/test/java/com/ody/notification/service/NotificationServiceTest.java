package com.ody.notification.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

import com.ody.common.BaseServiceTest;
import com.ody.mate.domain.Mate;
import com.ody.meeting.domain.Meeting;
import com.ody.member.domain.Member;
import com.ody.member.service.MemberService;
import com.ody.notification.domain.Notification;
import com.ody.notification.domain.NotificationStatus;
import com.ody.notification.domain.NotificationType;
import com.ody.notification.domain.types.Nudge;
import com.ody.notification.repository.NotificationRepository;
import com.ody.notification.service.event.NudgeEvent;
import com.ody.notification.service.event.PushEvent;
import com.ody.notification.service.event.UnSubscribeEvent;
import com.ody.route.service.RouteService;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.scheduling.TaskScheduler;

class NotificationServiceTest extends BaseServiceTest {

    @Autowired
    private NotificationService notificationService;

    @MockBean
    private RouteService routeService;

    @MockBean
    private TaskScheduler taskScheduler;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private MemberService memberService;

    @DisplayName("PENDING 상태의 알림들을 TaskScheduler로 스케줄링 한다.")
    @Test
    void schedulePendingNotification() {
        Mate mate = fixtureGenerator.generateMate();
        fixtureGenerator.generateNotification(mate, NotificationType.DEPARTURE_REMINDER, NotificationStatus.PENDING);
        fixtureGenerator.generateNotification(mate, NotificationType.DEPARTURE_REMINDER, NotificationStatus.DONE);

        notificationService.schedulePendingNotification();

        BDDMockito.verify(taskScheduler, Mockito.times(1))
                .schedule(any(Runnable.class), any(Instant.class));
    }

    @DisplayName("모임방에 대한 구독 취소 이벤트가 발행된다")
    @Test
    void unSubscribeTopic() {
        Member member = fixtureGenerator.generateMember();
        Meeting odyMeeting = fixtureGenerator.generateMeeting();
        Meeting sojuMeeting = fixtureGenerator.generateMeeting();
        Mate jojo = fixtureGenerator.generateMate(odyMeeting, member);
        Mate kaki = fixtureGenerator.generateMate(sojuMeeting, member);

        fixtureGenerator.generateNotification(jojo, NotificationType.DEPARTURE_REMINDER, NotificationStatus.DONE);
        fixtureGenerator.generateNotification(kaki, NotificationType.DEPARTURE_REMINDER, NotificationStatus.DONE);
        fixtureGenerator.generateNotification(kaki, NotificationType.ENTRY, NotificationStatus.DONE);

        notificationService.unSubscribeTopic(List.of(odyMeeting, sojuMeeting));

        assertThat(applicationEvents.stream(UnSubscribeEvent.class))
                .hasSize(2);
    }

    @DisplayName("재촉하기 이벤트가 발행된다")
    @Test
    void sendSendNudgeMessageMessage() {
        Meeting odyMeeting = fixtureGenerator.generateMeeting();
        Mate requestMate = fixtureGenerator.generateMate(odyMeeting);
        Mate nudgedMate = fixtureGenerator.generateMate(odyMeeting);
        Nudge nudge = new Nudge(nudgedMate);

        notificationService.sendNudgeMessage(requestMate, nudge);

        assertThat(applicationEvents.stream(NudgeEvent.class))
                .hasSize(1);
    }

    @DisplayName("특정 참여자의 전송 전 알람을 모두 DISMISSED 상태로 변경한다.")
    @Test
    void updateAllStatusPendingToDismissedByMateId() {
        Mate mate = fixtureGenerator.generateMate();
        LocalDateTime now = LocalDateTime.now();
        fixtureGenerator.generateNotification(mate, now.minusSeconds(1), NotificationStatus.DONE);
        fixtureGenerator.generateNotification(mate, now.minusSeconds(1), NotificationStatus.PENDING);
        fixtureGenerator.generateNotification(mate, now.plusSeconds(1), NotificationStatus.PENDING);

        notificationService.updateAllStatusToDismissByMateIdAndSendAtAfterNow(mate.getId());

        assertThat(notificationRepository.findAll()).extracting(Notification::getStatus).containsExactly(
                NotificationStatus.DONE,
                NotificationStatus.PENDING,
                NotificationStatus.DISMISSED
        );
    }

    @DisplayName("DISMISSED 상태이면 알림을 보내지 않는다.")
    @Test
    void sendPushNotificationFailure() {
        Notification dismissedNotification = fixtureGenerator.generateNotification(
                NotificationType.DEPARTURE_REMINDER,
                NotificationStatus.DISMISSED
        );

        notificationService.send(dismissedNotification);

        assertThat(applicationEvents.stream(PushEvent.class))
                .hasSize(0);
    }
}
