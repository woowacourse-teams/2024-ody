package com.ody.notification.service;

import com.ody.common.exception.OdyNotFoundException;
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
        DepartureTime sendAt = routeService.calculateDepartureTime(
                mate.getOrigin(),
                meeting.getTarget(),
                LocalDateTime.of(meeting.getDate(), meeting.getTime())
        );

        Notification notification = new Notification(
                mate,
                NotificationType.DEPARTURE_REMINDER,
                sendAt.getValue(),
                NotificationStatus.PENDING
        );
        Notification savedNotification = notificationRepository.save(notification);

        fcmSubscriber.subscribeTopic(meeting, deviceToken);

        FcmSendRequest fcmSendRequest = new FcmSendRequest(
                meeting.getId().toString(),
                savedNotification.getId(),
                LocalDateTime.now().plusSeconds(10) // TODO: savedNotification.getSendAt() 으로 변경
        );
        publisher.publishEvent(fcmSendRequest);
    }

    public List<Notification> findAllMeetingLogs(Long meetingId) {
        return notificationRepository.findAllMeetingLogs(meetingId);
    }

    public Notification findById(Long notificationId) {
        return notificationRepository.findById(notificationId)
                .orElseThrow(() -> new OdyNotFoundException("존재하지 않는 알림입니다."));
    }
}
