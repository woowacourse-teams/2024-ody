package com.ody.notification.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

import com.ody.common.BaseServiceTest;
import com.ody.common.Fixture;
import com.ody.mate.domain.Mate;
import com.ody.mate.domain.Nickname;
import com.ody.mate.repository.MateRepository;
import com.ody.meeting.domain.Meeting;
import com.ody.meeting.repository.MeetingRepository;
import com.ody.member.domain.Member;
import com.ody.member.repository.MemberRepository;
import com.ody.notification.domain.Notification;
import com.ody.notification.domain.NotificationType;
import com.ody.notification.repository.NotificationRepository;
import com.ody.route.domain.RouteTime;
import com.ody.route.service.RouteService;
import com.ody.util.TimeUtil;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

class NotificationServiceTest extends BaseServiceTest {

    @Autowired
    private NotificationService notificationService;

    @MockBean
    private RouteService routeService;

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
                new Mate(savedPastMeeting, member, new Nickname("제리"), Fixture.ORIGIN_LOCATION, 10L));
        RouteTime routeTime = new RouteTime(1);
        notificationService.saveAndSendNotifications(savedPastMeeting, mate, member.getDeviceToken(), routeTime);

        Optional<Notification> departureNotification = notificationRepository.findAll().stream()
                .filter(notification -> isDepartureReminder(notification) && isNow(notification))
                .findAny();

        assertThat(departureNotification).isPresent();
    }

    private boolean isDepartureReminder(Notification notification) {
        return notification.getType() == NotificationType.DEPARTURE_REMINDER;
    }

    private boolean isNow(Notification notification) {
        return TimeUtil.trimSecondsAndNanos(notification.getSendAt())
                .isEqual(TimeUtil.nowWithTrim());
    }

    @DisplayName("재촉하기 메시지가 발송된다")
    @Test
    void sendSendNudgeMessageMessage() {
        Member member = memberRepository.save(Fixture.MEMBER1);
        Meeting odyMeeting = meetingRepository.save(Fixture.ODY_MEETING);
        Mate mate = mateRepository.save(
                new Mate(odyMeeting, member, new Nickname("제리"), Fixture.ORIGIN_LOCATION, 10L)
        );

        notificationService.sendNudgeMessage(mate);

        Mockito.verify(getFcmPushSender(), times(1)).sendNudgeMessage(any());
    }
}
