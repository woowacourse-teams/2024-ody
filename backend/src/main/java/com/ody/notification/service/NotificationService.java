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
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {
    private static final ZoneOffset KST_OFFSET = ZoneOffset.ofHours(9);

    private final TaskScheduler taskScheduler;
    private final NotificationRepository notificationRepository;
    private final FcmSubscriber fcmSubscriber;
    private final RouteService routeService;
    private final FcmPushSender fcmPushSender;

    @Transactional
    public void saveAndSendDepartureReminder(Meeting meeting, Mate mate, DeviceToken deviceToken) {
        fcmSubscriber.subscribeTopic(meeting, deviceToken);
        saveAndSendEntryNotification(meeting, mate);
        Notification departureNotification = saveAndSendDepartureNotification(meeting, mate);
        departureNotification.updateDone();
    }

    private void saveAndSendEntryNotification(Meeting meeting, Mate mate) {
        Notification entryNotification = new Notification(
                mate,
                NotificationType.ENTRY,
                LocalDateTime.now().withNano(0),
                NotificationStatus.DONE
        );
        notificationRepository.save(entryNotification);

        FcmSendRequest fcmSendRequest = new FcmSendRequest(
                meeting.getId().toString(),
                NotificationType.ENTRY,
                mate.getNickname().getNickname(),
                LocalDateTime.now().withNano(0)
        );
        fcmPushSender.sendPushNotification(fcmSendRequest);
    }

    private Notification saveAndSendDepartureNotification(Meeting meeting, Mate mate) {
        DepartureTime sendAt = routeService.calculateDepartureTime(
                mate.getOrigin(),
                meeting.getTarget(),
                LocalDateTime.of(meeting.getDate(), meeting.getTime())
        );

        Notification departureNotification = new Notification(
                mate,
                NotificationType.DEPARTURE_REMINDER,
                sendAt.getValue(),
                NotificationStatus.PENDING
        );
        notificationRepository.save(departureNotification);

        FcmSendRequest fcmSendRequest = new FcmSendRequest(
                meeting.getId().toString(),
                NotificationType.DEPARTURE_REMINDER,
                mate.getNickname().getNickname(),
                sendAt.getValue()
        );
        Instant startTime = fcmSendRequest.sendAt().toInstant(KST_OFFSET);
        taskScheduler.schedule(() -> fcmPushSender.sendPushNotification(fcmSendRequest), startTime);
        return departureNotification;
    }

    public List<Notification> findAllMeetingLogs(Long meetingId) {
        return notificationRepository.findAllMeetingLogs(meetingId);
    }
}
