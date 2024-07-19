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

        FcmSendRequest fcmSendRequest = new FcmSendRequest(deviceToken, NotificationType.DEPARTURE_REMINDER, sendAt);
        publisher.publishEvent(fcmSendRequest);

        notification.updateDone();
    }
}
