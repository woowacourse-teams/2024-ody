package com.ody.notification.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.ody.common.Fixture;
import com.ody.common.config.JpaAuditingConfig;
import com.ody.mate.domain.Mate;
import com.ody.mate.domain.Nickname;
import com.ody.mate.repository.MateRepository;
import com.ody.meeting.domain.Location;
import com.ody.meeting.domain.Meeting;
import com.ody.meeting.repository.MeetingRepository;
import com.ody.member.domain.Member;
import com.ody.member.repository.MemberRepository;
import com.ody.notification.domain.Notification;
import com.ody.notification.domain.NotificationStatus;
import com.ody.notification.domain.NotificationType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Disabled;
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

        Location origin = Fixture.ORIGIN_LOCATION;
        Location target = Fixture.TARGET_LOCATION;
        Meeting meeting = new Meeting("모임1", LocalDate.now(), LocalTime.now(), target, "초대코드1");
        meetingRepository.save(meeting);

        Mate mate1 = mateRepository.save(new Mate(meeting, member1, new Nickname("은별"), origin));
        Mate mate2 = mateRepository.save(new Mate(meeting, member2, new Nickname("콜리"), origin));

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

        List<Notification> notifications = notificationRepository.findAllMeetingLogs(meeting.getId());

        assertThat(notifications.size()).isEqualTo(2);
    }
}
