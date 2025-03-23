package com.ody.notification.service;

import com.ody.mate.domain.Mate;
import com.ody.meeting.domain.Meeting;
import com.ody.member.domain.DeviceToken;
import com.ody.notification.domain.FcmTopic;
import com.ody.notification.domain.Notification;
import com.ody.notification.domain.NotificationStatus;
import com.ody.notification.domain.NotificationType;
import com.ody.notification.domain.types.Nudge;
import com.ody.notification.repository.NotificationRepository;
import com.ody.notification.service.event.NudgeEvent;
import com.ody.notification.service.event.PushEvent;
import com.ody.notification.service.event.SubscribeEvent;
import com.ody.notification.service.event.UnSubscribeEvent;
import com.ody.util.ScheduleRunner;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final FcmEventPublisher fcmEventPublisher;
    private final ScheduleRunner scheduleRunner;

    @Transactional
    public void saveAndSend(Notification notification) {
        Notification savedNotification = save(notification);
        sendNotification(savedNotification);
    }

    @Transactional
    public Notification save(Notification notification) {
        return notificationRepository.save(notification);
    }

    private void sendNotification(Notification notification) {
        PushEvent pushEvent = new PushEvent(this, notification);
        fcmEventPublisher.publishWithTransaction(pushEvent);
        log.info(
                "{} 타입 {} 상태 알림 {}에 발송 성공",
                notification.getType(),
                notification.getStatus(),
                notification.getSendAt()
        );
    }

    public void subscribeTopic(DeviceToken deviceToken, FcmTopic fcmTopic) {
        SubscribeEvent subscribeEvent = new SubscribeEvent(this, deviceToken, fcmTopic);
        fcmEventPublisher.publish(subscribeEvent);
    }

    @Transactional
    public void sendNudgeMessage(Mate requestMate, Nudge nudge) {
        Notification nudgeNotification = notificationRepository.save(nudge.toNotification());
        NudgeEvent nudgeEvent = new NudgeEvent(this, requestMate, nudgeNotification);
        fcmEventPublisher.publishWithTransaction(nudgeEvent);
    }

    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    public void schedulePendingNotification() {
        //TODO reserved Noti로 분리하면서 리팩터링 예정
        List<Notification> notifications = notificationRepository.findAllByTypeAndStatus(
                NotificationType.DEPARTURE_REMINDER,
                NotificationStatus.PENDING
        );
        notifications.forEach(notification -> scheduleRunner.runWithTransaction(
                        () -> sendNotification(notification),
                        notification.getSendAt()
                )
        );
        log.info("애플리케이션 시작 - PENDING 상태 출발 알림 {}개 스케줄링", notifications.size());
    }

    @Transactional
    public void updateAllStatusToDismissByMateIdAndSendAtAfterNow(long mateId) {
        notificationRepository.updateAllStatusToDismissedByMateIdAndSendAtAfterDateTime(mateId, LocalDateTime.now());
    }

    public void unSubscribeTopic(Meeting meeting, DeviceToken deviceToken) {
        FcmTopic fcmTopic = new FcmTopic(meeting);
        UnSubscribeEvent unSubscribeEvent = new UnSubscribeEvent(this, deviceToken, fcmTopic);
        fcmEventPublisher.publish(unSubscribeEvent);
    }

    public void unSubscribeTopic(List<Meeting> meetings) {
        for (Meeting meeting : meetings) {
            notificationRepository.findAllMeetingIdAndType(meeting.getId(), NotificationType.DEPARTURE_REMINDER)
                    .forEach(notification -> unSubscribeTopic(
                                    meeting,
                                    notification.getMate().getMember().getDeviceToken()
                            )
                    );
        }
    }
}
