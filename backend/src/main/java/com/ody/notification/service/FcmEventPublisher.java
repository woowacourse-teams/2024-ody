package com.ody.notification.service;

import com.ody.notification.domain.Notification;
import com.ody.notification.repository.NotificationRepository;
import com.ody.notification.service.event.NoticeEvent;
import com.ody.notification.service.event.NudgeEvent;
import com.ody.notification.service.event.PushEvent;
import com.ody.notification.service.event.SubscribeEvent;
import com.ody.notification.service.event.UnSubscribeEvent;
import com.ody.util.InstantConverter;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class FcmEventPublisher {

    private final TaskScheduler taskScheduler;
    private final ApplicationEventPublisher eventPublisher;
    private final NotificationRepository notificationRepository;

    @Transactional
    public void scheduleNotification2(Object source, Notification notification) {
        Instant startTime = InstantConverter.kstToInstant(notification.getSendAt());
        taskScheduler.schedule(() -> publishPushEvent(source, notification), startTime);
        log.info(
                "{} 타입 {} 상태 알림 {}에 스케줄링 예약",
                notification.getType(),
                notification.getStatus(),
                InstantConverter.instantToKst(startTime)
        );
    }

    private void publishPushEvent(Object source, Notification notification) {
        Notification savedNotification = notificationRepository.findById(notification.getId())
                .orElse(notification); // noti 생성과 동시에 실행되는 경우, 다른 트랜잭션이므로 즉시 findById 할 수 없어 기존 noti 사용

        if (savedNotification.isStatusDismissed()) {
            log.info("DISMISSED 상태 푸시 알림 전송 스킵 : {}", savedNotification);
            return;
        }
        eventPublisher.publishEvent(new PushEvent(source, savedNotification));
    }

    public void publishNudgeEvent(NudgeEvent nudgeEvent) {
        eventPublisher.publishEvent(nudgeEvent);
    }

    public void publishNoticeEvent(NoticeEvent noticeEvent) {
        eventPublisher.publishEvent(noticeEvent);
    }

    public void publishSubscribeEvent(SubscribeEvent subscribeEvent) {
        eventPublisher.publishEvent(subscribeEvent);
    }

    public void publishUnSubscribeEvent(UnSubscribeEvent unSubscribeEvent) {
        eventPublisher.publishEvent(unSubscribeEvent);
    }




}
