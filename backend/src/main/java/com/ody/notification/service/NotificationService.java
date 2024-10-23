package com.ody.notification.service;

import com.ody.common.aop.DisabledDeletedFilter;
import com.ody.mate.domain.Mate;
import com.ody.meeting.domain.Meeting;
import com.ody.member.domain.DeviceToken;
import com.ody.notification.domain.FcmTopic;
import com.ody.notification.domain.Notification;
import com.ody.notification.domain.NotificationStatus;
import com.ody.notification.domain.NotificationType;
import com.ody.notification.domain.message.DirectMessage;
import com.ody.notification.dto.response.NotiLogFindResponses;
import com.ody.notification.repository.NotificationRepository;
import com.ody.route.domain.DepartureTime;
import com.ody.util.InstantConverter;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final FcmSubscriber fcmSubscriber;
    private final FcmPushSender fcmPushSender;
    private final TaskScheduler taskScheduler;

    @Transactional
    public void saveAndSendNotifications(Meeting meeting, Mate mate, DeviceToken deviceToken) {
        FcmTopic fcmTopic = new FcmTopic(meeting);
        saveAndSendEntryNotification(mate, fcmTopic);
        fcmSubscriber.subscribeTopic(fcmTopic, deviceToken);
        saveAndSendDepartureReminderNotification(meeting, mate, fcmTopic);
    }

    private void saveAndSendEntryNotification(Mate mate, FcmTopic fcmTopic) {
        Notification notification = Notification.createEntry(mate, fcmTopic);
        saveAndSendNotification(notification);
    }

    private void saveAndSendDepartureReminderNotification(Meeting meeting, Mate mate, FcmTopic fcmTopic) {
        DepartureTime departureTime = new DepartureTime(meeting, mate.getEstimatedMinutes());
        LocalDateTime sendAt = calculateSendAt(departureTime);
        Notification notification = Notification.createDepartureReminder(mate, sendAt, fcmTopic);
        saveAndSendNotification(notification);
    }

    private LocalDateTime calculateSendAt(DepartureTime departureTime) {
        if (departureTime.isBefore(LocalDateTime.now())) {
            return LocalDateTime.now();
        }
        return departureTime.getValue();
    }

    private void saveAndSendNotification(Notification notification) {
        Notification savedNotification = notificationRepository.save(notification);
        scheduleNotification(savedNotification);
    }

    public void scheduleNotification(Notification notification) {
        Instant startTime = InstantConverter.kstToInstant(notification.getSendAt());
        taskScheduler.schedule(() -> fcmPushSender.sendPushNotification(notification), startTime);
        log.info(
                "{} 타입 {} 상태 알림 {}에 스케줄링 예약",
                notification.getType(),
                notification.getStatus(),
                InstantConverter.instantToKst(startTime)
        );
    }

    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    public void schedulePendingNotification() {
        List<Notification> notifications = notificationRepository.findAllByTypeAndStatus(
                NotificationType.DEPARTURE_REMINDER,
                NotificationStatus.PENDING
        );
        notifications.forEach(this::scheduleNotification);
        log.info("애플리케이션 시작 - PENDING 상태 출발 알림 {}개 스케줄링", notifications.size());
    }

    @DisabledDeletedFilter
    public NotiLogFindResponses findAllMeetingLogs(Long meetingId) {
        List<Notification> notifications = notificationRepository.findAllMeetingLogsBeforeThanEqual(
                meetingId,
                LocalDateTime.now()
        );
        return NotiLogFindResponses.from(notifications);
    }

    @Transactional
    public void sendNudgeMessage(Mate requestMate, Mate nudgedMate) {
        Notification nudgeNotification = notificationRepository.save(Notification.createNudge(nudgedMate));
        fcmPushSender.sendNudgeMessage(
                nudgeNotification,
                DirectMessage.createMessageToOther(requestMate, nudgeNotification)
        );
    }

    @Transactional
    public void updateAllStatusPendingToDismissedByMateId(long mateId) {
        List<Notification> notifications = notificationRepository.findAllByMateIdAndStatus(
                mateId,
                NotificationStatus.PENDING
        );
        notifications.forEach(Notification::updateStatusToDismissed);
    }

    @Transactional
    public void saveMemberDeletionNotification(Mate mate) {
        Notification notification = Notification.createMemberDeletion(mate);
        notificationRepository.save(notification);
    }

    @Transactional
    public void saveMateLeaveNotification(Mate mate) {
        Notification notification = Notification.createMateLeave(mate);
        notificationRepository.save(notification);
    }

    public void unSubscribeTopic(Meeting meeting, DeviceToken deviceToken) {
        FcmTopic fcmTopic = new FcmTopic(meeting);
        fcmSubscriber.unSubscribeTopic(fcmTopic, deviceToken);
    }

    public void unSubscribeTopic(List<Meeting> meetings) {
        for (Meeting meeting : meetings) {
            notificationRepository.findAllMeetingIdAndType(meeting.getId(), NotificationType.DEPARTURE_REMINDER)
                    .forEach(notification -> fcmSubscriber.unSubscribeTopic(
                                    notification.getFcmTopic(),
                                    notification.getMate().getMember().getDeviceToken()
                            )
                    );
        }
    }
}
