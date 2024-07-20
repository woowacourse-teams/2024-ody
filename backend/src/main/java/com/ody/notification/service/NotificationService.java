package com.ody.notification.service;

import com.ody.mate.domain.Mate;
import com.ody.meeting.domain.Meeting;
import com.ody.notification.domain.Notification;
import com.ody.notification.domain.NotificationStatus;
import com.ody.notification.domain.NotificationType;
import com.ody.notification.dto.request.FcmSendRequest;
import com.ody.notification.repository.NotificationRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final ApplicationEventPublisher publisher;
    private final NotificationRepository notificationRepository;

    @Transactional
    public void saveAndSendDepartureReminder(Meeting meeting, Mate mate, String deviceToken) {
        // TODO : RouteService가 해줄꺼임.
        LocalDate now = meeting.getDate();
        LocalTime departureTime = meeting.getTime().minusMinutes(2);
        LocalDateTime sendAt = LocalDateTime.of(now, departureTime);

        Notification notification = new Notification(
                mate,
                NotificationType.DEPARTURE_REMINDER,
                sendAt,
                NotificationStatus.PENDING
        );
        notificationRepository.save(notification);

        /* TODO
             모든 참여자들에게 알림이 가도록 meeting 방에 있는 모든 참여자들의 deviceToken들에 알림을 보내도록 수정해야함.
             이제 출발해야하는 참여자에게 "이제 나가야할 시간에요"라는 푸쉬 알림이 간다면
             다른 참여자에게 푸쉬 알림 메세지는 "이제 나가야할 시간에요"가 아닌 "oo가 이제 나갈 시간이에요" 라는 문구가 나가야할 텐데
             이를 안드로이드 측에서 처리할 수 있는 건지 ??
        */
        FcmSendRequest fcmSendRequest = new FcmSendRequest(deviceToken, NotificationType.DEPARTURE_REMINDER, sendAt);
        publisher.publishEvent(fcmSendRequest);

        notification.updateDone();
    }
}
