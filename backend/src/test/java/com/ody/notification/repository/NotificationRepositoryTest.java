package com.ody.notification.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.ody.mate.domain.Mate;
import com.ody.mate.domain.repository.MateRepository;
import com.ody.meeting.domain.Location;
import com.ody.meeting.domain.Meeting;
import com.ody.meeting.repository.MeetingRepository;
import com.ody.member.domain.Member;
import com.ody.member.repository.MemberRepository;
import com.ody.notification.domain.Notification;
import com.ody.notification.domain.NotificationType;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
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
        Location origin = new Location("출발지", "출발 위도", "출발 경도");
        Location target = new Location("도착지", "도착 위도", "도착 경도");
        Meeting meeting = new Meeting("모임1", LocalDate.now(), LocalTime.now(), target, "초대코드1");
        meetingRepository.save(meeting);

        Member member1 = new Member("token1");
        Member member2 = new Member("token2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        Mate mate1 = new Mate(meeting, member1, "은별", origin);
        Mate mate2 = new Mate(meeting, member1, "콜리", origin);
        mateRepository.save(mate1);
        mateRepository.save(mate2);

        Notification notification1 = new Notification(mate1, NotificationType.ENTRY);
        Notification notification2 = new Notification(mate2, NotificationType.DEPARTURE_REMINDER);
        notificationRepository.save(notification1);
        notificationRepository.save(notification2);

        List<Notification> notifications = notificationRepository.findAllMeetingLogsById(1L);

        assertThat(notifications.size()).isEqualTo(2);
    }
}
