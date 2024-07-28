package com.ody.notification.service;

import com.ody.common.exception.OdyNotFoundException;
import com.ody.mate.domain.Mate;
import com.ody.meeting.domain.Meeting;
import com.ody.member.domain.DeviceToken;
import com.ody.notification.domain.Notification;
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
    public void saveAndSendNotifications(Meeting meeting, Mate mate, DeviceToken deviceToken) {
        saveAndSendEntryNotification(meeting, mate);
        fcmSubscriber.subscribeTopic(meeting, deviceToken);
        saveAndSendDepartureReminderNotification(meeting, mate);
    }

    private void saveAndSendEntryNotification(Meeting meeting, Mate mate) {
        Notification notification = Notification.createEntry(mate);
        saveAndSendNotification(meeting, notification);
    }

    private void saveAndSendDepartureReminderNotification(Meeting meeting, Mate mate) {
        DepartureTime sendAt = calculateSendAt(meeting, mate);
        Notification notification = Notification.createDepartureReminder(mate, sendAt.getValue());
        saveAndSendNotification(meeting, notification);
    }

    private void saveAndSendNotification(Meeting meeting, Notification notification) {
        Notification savedNotification = notificationRepository.save(notification);

        FcmSendRequest fcmSendRequest = new FcmSendRequest(meeting, savedNotification);
        publisher.publishEvent(fcmSendRequest);
    }

    private DepartureTime calculateSendAt(Meeting meeting, Mate mate) {
        return routeService.calculateDepartureTime(
                mate.getOrigin(),
                meeting.getTarget(),
                LocalDateTime.of(meeting.getDate(), meeting.getTime())
        );
    }

    public List<Notification> findAllMeetingLogs(Long meetingId) {
        return notificationRepository.findAllMeetingLogs(meetingId);
    }

    public Notification findById(Long notificationId) {
        return notificationRepository.findById(notificationId)
                .orElseThrow(() -> new OdyNotFoundException("존재하지 않는 알림입니다."));
    }
}
