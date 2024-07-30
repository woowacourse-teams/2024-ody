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
import com.ody.notification.domain.Notification;
import com.ody.notification.domain.NotificationType;
import com.ody.notification.repository.NotificationRepository;
import com.ody.route.domain.DepartureTime;
import com.ody.route.domain.RouteTime;
import com.ody.route.service.RouteService;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
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
        Meeting meeting = meetingRepository.save(Fixture.ODY_MEETING1);
        Mate mate = mateRepository.save(new Mate(meeting, member, new Nickname("제리"), Fixture.ORIGIN_LOCATION));

        LocalDateTime today = LocalDateTime.now();
        DepartureTime pastDepartureTime = new DepartureTime(new RouteTime(1), today.minusDays(1));

        BDDMockito.given(routeService.calculateDepartureTime(any(), any(), any()))
                .willReturn(pastDepartureTime);

        notificationService.saveAndSendDepartureReminder(meeting, mate, member.getDeviceToken());

        Optional<Notification> departureNotification = notificationRepository.findAll().stream()
                .filter(notification -> isDepartureReminder(notification) && isNow(notification))
                .findAny();

        assertThat(departureNotification).isPresent();
    }

    private boolean isDepartureReminder(Notification notification) {
        return notification.getType() == NotificationType.DEPARTURE_REMINDER;
    }

    private boolean isNow(Notification notification) {
        return notification.getSendAt().withNano(0)
                .isEqual(LocalDateTime.now().withNano(0));
    }
}
