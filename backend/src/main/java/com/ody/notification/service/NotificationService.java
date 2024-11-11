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
    private final ApplicationEventPublisher eventPublisher;
    private final TaskScheduler taskScheduler;

    @Transactional
    public void saveAndSendNotifications(Meeting meeting, Mate mate, DeviceToken deviceToken) {
        FcmTopic fcmTopic = new FcmTopic(meeting);

        Notification entryNotification = Notification.createEntry(mate, fcmTopic);
        saveAndScheduleNotification(entryNotification);

        SubscribeEvent subscribeEvent = new SubscribeEvent(this, deviceToken, fcmTopic);
        eventPublisher.publishEvent(subscribeEvent);

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
        taskScheduler.schedule(() -> sendNotification(notification), startTime);
        log.info(
                "{} 타입 {} 상태 알림 {}에 스케줄링 예약",
                notification.getType(),
                notification.getStatus(),
                InstantConverter.instantToKst(startTime)
        );
    }

    private void sendNotification(Notification notification) {
        Notification savedNotification = notificationRepository.findById(notification.getId())
                .orElse(notification); // noti 생성과 동시에 실행되는 경우, 다른 트랜잭션이므로 즉시 findById 할 수 없어 기존 noti 사용

        if (savedNotification.isStatusDismissed()) {
            log.info("DISMISSED 상태 푸시 알림 전송 스킵 : {}", savedNotification);
            return;
        }
        eventPublisher.publishEvent(new PushEvent(this, savedNotification));
    }

    @Transactional
    public void sendNudgeMessage(Mate requestMate, Mate nudgedMate) {
        Notification nudgeNotification = notificationRepository.save(Notification.createNudge(nudgedMate));
        eventPublisher.publishEvent(new NudgeEvent(this, requestMate, nudgeNotification));
    }

    public void scheduleNotice(GroupMessage groupMessage, LocalDateTime noticeTime) {
        Instant startTime = InstantConverter.kstToInstant(noticeTime);
        taskScheduler.schedule(() -> eventPublisher.publishEvent(new NoticeEvent(this, groupMessage)), startTime);
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
        eventPublisher.publishEvent(unSubscribeEvent);
    }

    public void unSubscribeTopic2(List<Meeting> meetings) {
        for (Meeting meeting : meetings) {
            notificationRepository.findAllMeetingIdAndType(meeting.getId(), NotificationType.DEPARTURE_REMINDER)
                    .forEach(notification -> unSubscribeTopic2(
                                    meeting,
                                    notification.getMate().getMember().getDeviceToken()
                            )
                    );
        }
    }
}
