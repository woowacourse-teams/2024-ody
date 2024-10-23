package com.ody.notification.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.ody.common.BaseRepositoryTest;
import com.ody.common.Fixture;
import com.ody.mate.domain.Mate;
import com.ody.mate.domain.Nickname;
import com.ody.meeting.domain.Meeting;
import com.ody.member.domain.Member;
import com.ody.notification.domain.FcmTopic;
import com.ody.notification.domain.Notification;
import com.ody.notification.domain.NotificationStatus;
import com.ody.notification.domain.NotificationType;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NotificationRepositoryTest extends BaseRepositoryTest {

    @DisplayName("특정 모임의 알림 발송 시간이 지나지 않은 알림 전송 시간을 기준하여 오름차순으로 반환한다.")
    @Test
    void findAllMeetingLogsBeforeThanEqual() {
        Member member = memberRepository.save(Fixture.MEMBER1);
        Meeting odyMeeting = meetingRepository.save(Fixture.ODY_MEETING);
        Mate mate = fixtureGenerator.generateMate(odyMeeting, member);

        Notification entryNotification = fixtureGenerator.generateNotification(
                mate,
                NotificationType.ENTRY,
                LocalDateTime.now(),
                NotificationStatus.DONE
        );
        Notification departurnReminderNotification = fixtureGenerator.generateNotification(
                mate,
                NotificationType.DEPARTURE_REMINDER,
                LocalDateTime.now(),
                NotificationStatus.PENDING
        );

        List<Notification> notifications = notificationRepository.findAllByMeetingIdAndSentAtBeforeDateTimeAndStatusIsNotDismissed(
                odyMeeting.getId(),
                LocalDateTime.now()
        );

        assertThat(notifications).containsExactly(entryNotification, departurnReminderNotification);
    }

    @DisplayName("현재 시간 이전의 모임 notification만 가져온다")
    @Test
    void findAllNotificationsById() {
        Member member1 = memberRepository.save(Fixture.MEMBER1);
        Member member2 = memberRepository.save(Fixture.MEMBER2);

        Meeting odyMeeting = meetingRepository.save(Fixture.ODY_MEETING);

        Mate mate1 = mateRepository.save(
                new Mate(odyMeeting, member1, new Nickname("은별"), Fixture.ORIGIN_LOCATION, 10L)
        );
        Mate mate2 = mateRepository.save(
                new Mate(odyMeeting, member2, new Nickname("콜리"), Fixture.ORIGIN_LOCATION, 10L)
        );

        Notification pastNotification = new Notification(
                mate1,
                NotificationType.DEPARTURE_REMINDER,
                LocalDateTime.now().minusMinutes(30),
                NotificationStatus.DONE,
                new FcmTopic(odyMeeting)
        );
        Notification futureNotification = new Notification(
                mate2,
                NotificationType.DEPARTURE_REMINDER,
                LocalDateTime.now().plusMinutes(30),
                NotificationStatus.PENDING,
                new FcmTopic(odyMeeting)
        );
        notificationRepository.save(pastNotification);
        notificationRepository.save(futureNotification);

        List<Notification> notifications = notificationRepository.findAllByMeetingIdAndSentAtBeforeDateTimeAndStatusIsNotDismissed(
                odyMeeting.getId(),
                LocalDateTime.now()
        );

        assertThat(notifications.size()).isOne();
    }

    @DisplayName("알림 타입과 알림 상태 조건을 가진 알림 리스트를 조회한다")
    @Test
    void findAllByTypeAndStatus() {
        Member member = memberRepository.save(Fixture.MEMBER1);
        Meeting odyMeeting = meetingRepository.save(Fixture.ODY_MEETING);
        Mate mate = mateRepository.save(
                new Mate(odyMeeting, member, new Nickname("은별"), Fixture.ORIGIN_LOCATION, 10L)
        );

        Notification departureReminderAndPendingNotification = new Notification(
                mate,
                NotificationType.DEPARTURE_REMINDER,
                LocalDateTime.now(),
                NotificationStatus.PENDING,
                new FcmTopic(odyMeeting)
        );
        Notification entryAndDoneNotification = new Notification(
                mate,
                NotificationType.ENTRY,
                LocalDateTime.now(),
                NotificationStatus.DONE,
                new FcmTopic(odyMeeting)
        );
        notificationRepository.save(departureReminderAndPendingNotification);
        notificationRepository.save(entryAndDoneNotification);

        List<Notification> departureReminderAndPendingNotifications = notificationRepository.findAllByTypeAndStatus(
                NotificationType.DEPARTURE_REMINDER, NotificationStatus.PENDING
        );
        List<Notification> entryAndDoneNotifications = notificationRepository.findAllByTypeAndStatus(
                NotificationType.DEPARTURE_REMINDER, NotificationStatus.PENDING
        );

        assertAll(
                () -> assertThat(departureReminderAndPendingNotifications).hasSize(1),
                () -> assertThat(entryAndDoneNotifications).hasSize(1)
        );
    }

    @DisplayName("약속 ID와 알림 타입으로 알림 리스트를 조회한다")
    @Test
    void findAllMeetingIdAndType() {
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
        notificationRepository.save(notification1);
        notificationRepository.save(notification2);

        List<Notification> notifications = notificationRepository.findAllMeetingIdAndType(
                odyMeeting.getId(),
                NotificationType.DEPARTURE_REMINDER
        );

        assertAll(
                () -> assertThat(notifications).hasSize(1),
                () -> assertThat(notifications.get(0).getId()).isEqualTo(notification1.getId())
        );
    }
}
