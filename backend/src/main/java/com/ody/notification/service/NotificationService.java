package com.ody.notification.service;

import com.ody.mate.domain.Mate;
import com.ody.meeting.domain.Meeting;
import com.ody.member.domain.DeviceToken;
import com.ody.notification.domain.Notification;
import com.ody.notification.domain.NotificationStatus;
import com.ody.notification.domain.NotificationType;
import com.ody.notification.dto.request.FcmSendRequest;
import com.ody.notification.repository.NotificationRepository;
import com.ody.route.domain.DepartureTime;
import com.ody.route.service.RouteService;
import java.time.LocalDateTime;
import java.util.List;
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
    private final FcmSubscriber fcmSubscriber;
    private final RouteService routeService;

    @Transactional
    public void saveAndSendDepartureReminder(Meeting meeting, Mate mate, DeviceToken deviceToken) {
        LocalDateTime sendAt = calculateSendAt(meeting, mate);

        Notification notification = new Notification(
                mate,
                NotificationType.DEPARTURE_REMINDER,
                sendAt,
                NotificationStatus.PENDING
        );
        notificationRepository.save(notification);

        fcmSubscriber.subscribeTopic(meeting, deviceToken);

        FcmSendRequest fcmSendRequest = new FcmSendRequest(
                meeting.getId().toString(),
                NotificationType.DEPARTURE_REMINDER,
                mate.getNickname().getNickname(),
                sendAt
        );
        publisher.publishEvent(fcmSendRequest);

        notification.updateDone();
    }

    private LocalDateTime calculateSendAt(Meeting meeting, Mate mate) {
        DepartureTime sendAt = routeService.calculateDepartureTime(
                mate.getOrigin(),
                meeting.getTarget(),
                LocalDateTime.of(meeting.getDate(), meeting.getTime())
        );
        if (sendAt.getValue().isBefore(LocalDateTime.now())) {
            return LocalDateTime.now();
        }
        return sendAt.getValue();
    }

    public List<Notification> findAllMeetingLogs(Long meetingId) {
        return notificationRepository.findAllMeetingLogs(meetingId);
    }
}
