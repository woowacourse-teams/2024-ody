package com.ody.notification.service;

import com.ody.common.aop.DisabledDeletedFilter;
import com.ody.mate.domain.Mate;
import com.ody.meeting.domain.Meeting;
import com.ody.member.domain.DeviceToken;
import com.ody.notification.domain.FcmTopic;
import com.ody.notification.domain.Notification;
import com.ody.notification.domain.NotificationStatus;
import com.ody.notification.domain.NotificationType;
import com.ody.notification.domain.message.GroupMessage;
import com.ody.notification.dto.response.NotiLogFindResponses;
import com.ody.notification.repository.NotificationRepository;
import com.ody.notification.service.event.NoticeEvent;
import com.ody.notification.service.event.NudgeEvent;
import com.ody.notification.service.event.PushEvent;
import com.ody.notification.service.event.SubscribeEvent;
import com.ody.notification.service.event.UnSubscribeEvent;
import com.ody.route.domain.DepartureTime;
import com.ody.util.InstantConverter;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
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
    private final FcmEventPublisher fcmEventPublisher;
    private final TaskScheduler taskScheduler;

    @Transactional
    public void saveAndSendNotifications(Meeting meeting, Mate mate, DeviceToken deviceToken) {
        FcmTopic fcmTopic = new FcmTopic(meeting);

        Notification entryNotification = Notification.createEntry(mate, fcmTopic);
        saveAndScheduleNotification(entryNotification);

        SubscribeEvent subscribeEvent = new SubscribeEvent(this, deviceToken, fcmTopic);
        fcmEventPublisher.publish(subscribeEvent);

        saveAndSendDepartureReminderNotification(meeting, mate, fcmTopic);
    }

    private void saveAndSendDepartureReminderNotification(Meeting meeting, Mate mate, FcmTopic fcmTopic) {
        DepartureTime departureTime = new DepartureTime(meeting, mate.getEstimatedMinutes());
        LocalDateTime sendAt = calculateSendAt(departureTime);
        Notification notification = Notification.createDepartureReminder(mate, sendAt, fcmTopic);
        saveAndScheduleNotification(notification);
    }

    private LocalDateTime calculateSendAt(DepartureTime departureTime) {
        if (departureTime.isBefore(LocalDateTime.now())) {
            return LocalDateTime.now();
        }
        return departureTime.getValue();
    }

    private void saveAndScheduleNotification(Notification notification) {
        Notification savedNotification = notificationRepository.save(notification);
        scheduleNotification(savedNotification);
    }

    @Transactional
    public void scheduleNotification(Notification notification) {
        Instant startTime = InstantConverter.kstToInstant(notification.getSendAt());
        taskScheduler.schedule(() -> fcmEventPublisher.publishWithTransaction(new PushEvent(this, notification)), startTime);
        log.info(
                "{} 타입 {} 상태 알림 {}에 스케줄링 예약",
                notification.getType(),
                notification.getStatus(),
                InstantConverter.instantToKst(startTime)
        );
    }

    @Transactional
    public void sendNudgeMessage(Mate requestMate, Mate nudgedMate) {
        Notification nudgeNotification = notificationRepository.save(Notification.createNudge(nudgedMate));
        NudgeEvent nudgeEvent = new NudgeEvent(this, requestMate, nudgeNotification);
        fcmEventPublisher.publishWithTransaction(nudgeEvent);
    }

    public void scheduleNotice(GroupMessage groupMessage, LocalDateTime noticeTime) {
        Instant startTime = InstantConverter.kstToInstant(noticeTime);
        NoticeEvent noticeEvent = new NoticeEvent(this, groupMessage);
        taskScheduler.schedule(() -> fcmEventPublisher.publish(noticeEvent), startTime);
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

    @Transactional
    public Notification save(Notification notification) {
        return notificationRepository.save(notification);
    }

    @DisabledDeletedFilter
    public NotiLogFindResponses findAllNotiLogs(Long meetingId) {
        List<Notification> notifications = notificationRepository.findAllByMeetingIdAndSentAtBeforeDateTimeAndStatusIsNotDismissed(
                meetingId,
                LocalDateTime.now()
        );
        return NotiLogFindResponses.from(notifications);
    }

    @Transactional
    public void updateAllStatusToDismissByMateIdAndSendAtAfterNow(long mateId) {
        notificationRepository.updateAllStatusToDismissedByMateIdAndSendAtAfterDateTime(mateId, LocalDateTime.now());
    }

    public void unSubscribeTopic2(Meeting meeting, DeviceToken deviceToken) {
        FcmTopic fcmTopic = new FcmTopic(meeting);
        UnSubscribeEvent unSubscribeEvent = new UnSubscribeEvent(this, deviceToken, fcmTopic);
        fcmEventPublisher.publish(unSubscribeEvent);
    }

    public void unSubscribeTopic2(List<Meeting> meetings) {
        for (Meeting meeting : meetings) {

            List<Notification> allMeetingIdAndType = notificationRepository.findAllMeetingIdAndType(meeting.getId(),
                    NotificationType.DEPARTURE_REMINDER);

            notificationRepository.findAllMeetingIdAndType(meeting.getId(), NotificationType.DEPARTURE_REMINDER)
                    .forEach(notification -> unSubscribeTopic2(
                                    meeting,
                                    notification.getMate().getMember().getDeviceToken()
                            )
                    );
        }
    }
}
