package com.ody.notification.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

import com.ody.common.BaseServiceTest;
import com.ody.common.Fixture;
import com.ody.mate.domain.Mate;
import com.ody.mate.domain.Nickname;
import com.ody.mate.repository.MateRepository;
import com.ody.meeting.domain.Meeting;
import com.ody.meeting.repository.MeetingRepository;
import com.ody.member.domain.Member;
import com.ody.member.repository.MemberRepository;
import com.ody.notification.domain.FcmTopic;
import com.ody.notification.domain.Notification;
import com.ody.notification.domain.NotificationStatus;
import com.ody.notification.domain.NotificationType;
import com.ody.notification.repository.NotificationRepository;
import com.ody.route.service.RouteService;
import com.ody.util.TimeUtil;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
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
    private MemberRepository memberRepository;

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private MateRepository mateRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @DisplayName("알림 생성 시점이 전송 시점보다 늦은 경우 즉시 전송된다")
    @Test
    void sendImmediatelyIfDepartureTimeIsPast() {
        Member member = memberRepository.save(Fixture.MEMBER1);
        Meeting pastMeeting = new Meeting(
                "오디",
                LocalDate.now().minusDays(1),
                LocalTime.parse("14:00"),
                Fixture.TARGET_LOCATION,
                "초대코드"
        );
        Meeting savedPastMeeting = meetingRepository.save(pastMeeting);
        Mate mate = mateRepository.save(
                new Mate(savedPastMeeting, member, new Nickname("제리"), Fixture.ORIGIN_LOCATION, 1L)
        );
        notificationService.saveAndSendNotifications(savedPastMeeting, mate, member.getDeviceToken());

        Optional<Notification> departureNotification = notificationRepository.findAll().stream()
                .filter(notification -> notification.isDepartureReminder() && notification.isNow())
                .findAny();

        assertThat(departureNotification).isPresent();
    }

    @DisplayName("PENDING 상태의 알림들을 TaskScheduler로 스케줄링 한다.")
    @Test
    void schedulePendingNotification() {
        Meeting odyMeeting = meetingRepository.save(Fixture.ODY_MEETING);
        Member member = memberRepository.save(Fixture.MEMBER1);
        Mate mate = mateRepository.save(new Mate(odyMeeting, member, new Nickname("제리"), Fixture.ORIGIN_LOCATION, 10L));

        notificationRepository.save(new Notification(
                mate,
                NotificationType.DEPARTURE_REMINDER,
                LocalDateTime.now(),
                NotificationStatus.PENDING,
                new FcmTopic(odyMeeting)
        ));
        notificationRepository.save(new Notification(
                mate,
                NotificationType.DEPARTURE_REMINDER,
                LocalDateTime.now(),
                NotificationStatus.DONE,
                new FcmTopic(odyMeeting)
        ));

        notificationService.schedulePendingNotification();

        BDDMockito.verify(taskScheduler, Mockito.times(1))
                .schedule(any(Runnable.class), any(Instant.class));
    }

    @DisplayName("모임방에 대한 구독을 취소한다")
    @Test
    void unSubscribeTopic() {
        Member member = memberRepository.save(Fixture.MEMBER1);
        Meeting odyMeeting = meetingRepository.save(Fixture.ODY_MEETING);
        Meeting sojuMeeting = meetingRepository.save(Fixture.SOJU_MEETING);
        Mate jojo = mateRepository.save(
                new Mate(odyMeeting, member, new Nickname("은별"), Fixture.ORIGIN_LOCATION, 10L)
        );
        Mate kaki = mateRepository.save(
                new Mate(sojuMeeting, member, new Nickname("카키"), Fixture.ORIGIN_LOCATION, 10L)
        );

        Notification notification1 = new Notification(
                jojo,
                NotificationType.DEPARTURE_REMINDER,
                LocalDateTime.now(),
                NotificationStatus.DONE,
                new FcmTopic(odyMeeting)
        );
        Notification notification2 = new Notification(
                kaki,
                NotificationType.DEPARTURE_REMINDER,
                LocalDateTime.now(),
                NotificationStatus.DONE,
                new FcmTopic(sojuMeeting)
        );
        Notification notification3 = new Notification(
                kaki,
                NotificationType.ENTRY,
                LocalDateTime.now(),
                NotificationStatus.DONE,
                new FcmTopic(sojuMeeting)
        );
        notificationRepository.save(notification1);
        notificationRepository.save(notification2);
        notificationRepository.save(notification3);

        notificationService.unSubscribeTopic(List.of(odyMeeting, sojuMeeting));

        BDDMockito.verify(fcmSubscriber, Mockito.times(2)).unSubscribeTopic(any(), any());
    }

    @DisplayName("재촉하기 메시지가 발송된다")
    @Test
    void sendSendNudgeMessageMessage() {
        Member member1 = memberRepository.save(Fixture.MEMBER1);
        Member member2 = memberRepository.save(Fixture.MEMBER2);
        Meeting odyMeeting = meetingRepository.save(Fixture.ODY_MEETING);
        Mate requestMate = mateRepository.save(
                new Mate(odyMeeting, member1, new Nickname("제리"), Fixture.ORIGIN_LOCATION, 10L)
        );
        Mate nudgedMate = mateRepository.save(
                new Mate(odyMeeting, member2, new Nickname("콜리"), Fixture.ORIGIN_LOCATION, 10L)
        );

        notificationService.sendNudgeMessage(requestMate, nudgedMate);

        Mockito.verify(fcmPushSender, Mockito.times(1)).sendNudgeMessage(any(), any());
    }
}
