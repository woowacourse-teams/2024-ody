package com.ody.notification.service;

import com.ody.mate.domain.Mate;
import com.ody.meeting.domain.Meeting;
import com.ody.member.domain.DeviceToken;
import com.ody.notification.domain.FcmTopic;
import com.ody.notification.domain.Notification;
import com.ody.notification.domain.message.NudgeMessage;
import com.ody.notification.dto.request.FcmSendRequest;
import com.ody.notification.repository.NotificationRepository;
import com.ody.route.domain.DepartureTime;
import com.ody.route.domain.RouteTime;
import com.ody.util.TimeUtil;
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

    private final NotificationRepository notificationRepository;
    private final FcmSubscriber fcmSubscriber;
    private final FcmPushSender fcmPushSender;
    private final TaskScheduler taskScheduler;

    @Transactional
    public void saveAndSendNotifications(
            Meeting meeting,
            Mate mate,
            DeviceToken deviceToken,
            RouteTime routeTime
    ) {
        saveAndSendEntryNotification(meeting, mate);
        fcmSubscriber.subscribeTopic(new FcmTopic(meeting), deviceToken);
        saveAndSendDepartureReminderNotification(meeting, mate, routeTime);
    }

    private void saveAndSendEntryNotification(Meeting meeting, Mate mate) {
        Notification notification = Notification.createEntry(mate);
        saveAndSendNotification(meeting, notification);
    }

    private void saveAndSendDepartureReminderNotification(Meeting meeting, Mate mate, RouteTime routeTime) {
        DepartureTime departureTime = new DepartureTime(routeTime, meeting);
        LocalDateTime sendAt = calculateSendAt(departureTime);
        Notification notification = Notification.createDepartureReminder(mate, sendAt);
        saveAndSendNotification(meeting, notification);
    }

    private LocalDateTime calculateSendAt(DepartureTime departureTime) {
        if (departureTime.isBefore(LocalDateTime.now())) {
            return TimeUtil.nowWithTrim();
        }
        return TimeUtil.trimSecondsAndNanos(departureTime.getValue());
    }

    private void saveAndSendNotification(Meeting meeting, Notification notification) {
        Notification savedNotification = notificationRepository.save(notification);
        FcmSendRequest fcmSendRequest = new FcmSendRequest(new FcmTopic(meeting), savedNotification);
        scheduleNotification(fcmSendRequest);
    }

    private void scheduleNotification(FcmSendRequest fcmSendRequest) {
        Instant startTime = fcmSendRequest.notification().getSendAt().toInstant(KST_OFFSET);
        taskScheduler.schedule(() -> fcmPushSender.sendPushNotification(fcmSendRequest), startTime);
    }

    public List<Notification> findAllMeetingLogs(Long meetingId) {
        return notificationRepository.findAllMeetingLogs(meetingId);
    }

    @Transactional
    public void sendNudgeMessage(Mate requestMate, Mate nudgedMate) {
        Notification notification = Notification.createNudge(nudgedMate);
        Notification nudgeNotification = notificationRepository.save(notification);
        NudgeMessage nudgeMessage = new NudgeMessage(nudgedMate.getMember().getDeviceToken(),
                requestMate.getNicknameValue());
        fcmPushSender.sendNudgeMessage(nudgeNotification, nudgeMessage);
    }
}
