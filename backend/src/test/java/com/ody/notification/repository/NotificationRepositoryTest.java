package com.ody.notification.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.ody.common.Fixture;
import com.ody.common.config.JpaAuditingConfig;
import com.ody.mate.domain.Mate;
import com.ody.mate.repository.MateRepository;
import com.ody.meeting.domain.Meeting;
import com.ody.meeting.repository.MeetingRepository;
import com.ody.member.domain.DeviceToken;
import com.ody.member.domain.Member;
import com.ody.member.repository.MemberRepository;
import com.ody.notification.domain.Notification;
import com.ody.notification.domain.NotificationStatus;
import com.ody.notification.domain.NotificationType;
import com.ody.util.TimeUtil;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(JpaAuditingConfig.class)
class NotificationRepositoryTest {

    @Autowired
    MeetingRepository meetingRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    MateRepository mateRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @DisplayName("특정 모임의 Notification 반환")
    @Test
    void findAllMeetingLogsById() {
        Member member1 = memberRepository.save(Fixture.MEMBER1);
        Member member2 = memberRepository.save(Fixture.MEMBER2);

        Meeting odyMeeting = meetingRepository.save(Fixture.ODY_MEETING);

        Mate mate1 = generateMate(odyMeeting, member1);
        Mate mate2 = generateMate(odyMeeting, member2);

        Notification notification1 = new Notification(
                mate1,
                NotificationType.DEPARTURE_REMINDER,
                LocalDateTime.now(),
                NotificationStatus.DONE
        );
        Notification notification2 = new Notification(
                mate2,
                NotificationType.DEPARTURE_REMINDER,
                LocalDateTime.now(),
                NotificationStatus.PENDING
        );
        notificationRepository.save(notification1);
        notificationRepository.save(notification2);

        List<Notification> notifications = notificationRepository.findAllMeetingLogs(odyMeeting.getId());

        assertThat(notifications.size()).isEqualTo(2);
    }

    @DisplayName("현재 시간 이전의 모임 notification만 가져온다")
    @Test
    void findAllNotificationsById() {
        Member member1 = memberRepository.save(Fixture.MEMBER1);
        Member member2 = memberRepository.save(Fixture.MEMBER2);

        Meeting odyMeeting = meetingRepository.save(Fixture.ODY_MEETING);

        Mate mate1 = generateMate(odyMeeting, member1);
        Mate mate2 = generateMate(odyMeeting, member2);

        Notification pastNotification = new Notification(
                mate1,
                NotificationType.DEPARTURE_REMINDER,
                LocalDateTime.now().minusMinutes(30),
                NotificationStatus.DONE
        );
        Notification futureNotification = new Notification(
                mate2,
                NotificationType.DEPARTURE_REMINDER,
                LocalDateTime.now().plusMinutes(30),
                NotificationStatus.PENDING
        );
        notificationRepository.save(pastNotification);
        notificationRepository.save(futureNotification);

        List<Notification> notifications = notificationRepository.findAllMeetingLogs(odyMeeting.getId());

        assertThat(notifications.size()).isOne();
    }

    @DisplayName("특정 참여자의 A 상태인 알람을 모두 B 상태로 변경한다.")
    @Test
    void updateStatusFromTargetToNewByMateId() {
        Mate mate = generateMate();
        generateNotification(mate, NotificationStatus.PENDING);
        generateNotification(mate, NotificationStatus.DONE);
        generateNotification(mate, NotificationStatus.DISMISSED);
        generateNotification(mate, NotificationStatus.PENDING);

        notificationRepository.updateStatusFromTargetToNewByMateId(
                NotificationStatus.PENDING,
                NotificationStatus.DISMISSED,
                mate.getId()
        );
        List<NotificationStatus> actual = notificationRepository.findAll().stream()
                .map(Notification::getStatus)
                .toList();

        assertThat(actual).containsExactly(
                NotificationStatus.DISMISSED,
                NotificationStatus.DONE,
                NotificationStatus.DISMISSED,
                NotificationStatus.DISMISSED
        );
    }

    private Meeting generateMeeting() {
        LocalDateTime now = TimeUtil.nowWithTrim();
        return meetingRepository.save(new Meeting(
                "name",
                now.toLocalDate(),
                now.toLocalTime(),
                Fixture.TARGET_LOCATION,
                "invitecode"
        ));
    }

    private Member generateMember() {
        return memberRepository.save(new Member(
                "providerid",
                "nickname",
                "imageurl",
                new DeviceToken("devicetoken")
        ));
    }

    private Mate generateMate() {
        Meeting meeting = generateMeeting();
        Member member = generateMember();
        return generateMate(meeting, member);
    }

    private Mate generateMate(Meeting meeting, Member member) {
        return mateRepository.save(new Mate(meeting, member, Fixture.ORIGIN_LOCATION, 10L));
    }

    private Notification generateNotification(Mate mate, NotificationStatus notificationStatus) {
        LocalDateTime now = TimeUtil.nowWithTrim();
        return notificationRepository.save(new Notification(mate, NotificationType.ENTRY, now, notificationStatus));
    }
}
