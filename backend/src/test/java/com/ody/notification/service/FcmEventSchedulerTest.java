package com.ody.notification.service;

import static com.ody.common.Fixture.MEMBER1;
import static com.ody.common.Fixture.ODY_MEETING;
import static com.ody.common.Fixture.ORIGIN_LOCATION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

import com.ody.common.BaseServiceTest;
import com.ody.mate.domain.Mate;
import com.ody.mate.domain.Nickname;
import com.ody.mate.repository.MateRepository;
import com.ody.meeting.domain.Meeting;
import com.ody.meeting.repository.MeetingRepository;
import com.ody.member.domain.Member;
import com.ody.member.repository.MemberRepository;
import com.ody.notification.domain.Notification;
import com.ody.notification.domain.NotificationStatus;
import com.ody.notification.domain.NotificationType;
import com.ody.notification.dto.request.FcmSendRequest;
import com.ody.notification.repository.NotificationRepository;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class FcmEventSchedulerTest extends BaseServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private MateRepository mateRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @DisplayName("예약 알림이 2초 후에 전송된다")
    @Test
    void testScheduledNotificationIsSentAtCorrectTime() throws InterruptedException {
        Meeting meeting = meetingRepository.save(ODY_MEETING);
        Member member = memberRepository.save(MEMBER1);
        Mate mate = mateRepository.save(new Mate(meeting, member, new Nickname("제리"), ORIGIN_LOCATION, 10L));

        LocalDateTime sendAt = LocalDateTime.now().plusSeconds(2);
        Notification notification = notificationRepository.save(new Notification(
                mate,
                NotificationType.DEPARTURE_REMINDER,
                sendAt,
                NotificationStatus.PENDING
        ));
        FcmSendRequest fcmSendRequest = new FcmSendRequest(meeting.getId().toString(), notification);

        // 비동기 작업을 동기화 시키기 위한 클래스
        // 파라미터 인자에 비동기 작업의 개수를 입력해준다.
        // 입력된 개수의 비동기 작업이 종료될 때 까지 스레드는 대기 한다.
        CountDownLatch latch = new CountDownLatch(1);

        // Mokito의 doAnswer()는 특정 메서드가 호출될 때 수행할 작업을 정의한다.
        // fcmPushSender의 sendPushNotification 메서드가 호출될 때, latch.countDown()을 호출하여 카운트를 감소시킨다.
        // latch가 현재 1로 설정되어 있기 때문에 카운트가 감소되어 0개가 되면 대기하고 있던 스레드가 계속 작업을 진행할 수 있게 된다.
        doAnswer(invocation -> {
            latch.countDown();
            return null;
        }).when(getFcmPushSender()).sendPushNotification(fcmSendRequest);

        // 2초후에 메세지가 가도록 설정한 fcmSendRequest를 인자로 넣어 실제 sendPushNotification()를 호출한다.
        getFcmPushSender().sendPushNotification(fcmSendRequest);

        // latch의 카운트가 0이될 때까지 대기할 시간을 정의한다.
        assertThat(latch.await(2, TimeUnit.SECONDS)).isTrue();

        // sendPushNotification() 메서드가 호출되었는지 검증한다.
        verify(getFcmPushSender()).sendPushNotification(fcmSendRequest);
    }
}
