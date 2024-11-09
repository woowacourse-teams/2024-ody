package com.ody.notification.service;

import com.ody.common.aop.DisabledDeletedFilter;
import com.ody.mate.domain.Mate;
import com.ody.meeting.domain.Meeting;
import com.ody.member.domain.DeviceToken;
import com.ody.notification.domain.FcmTopic;
import com.ody.notification.domain.Notification;
import com.ody.notification.domain.NotificationType;
import com.ody.notification.domain.message.GroupMessage;
import com.ody.notification.dto.response.NotiLogFindResponses;
import com.ody.notification.repository.NotificationRepository;
import com.ody.notification.service.event.NoticeEvent;
import com.ody.notification.service.event.NudgeEvent;
import com.ody.notification.service.event.SubscribeEvent;
import com.ody.notification.service.event.UnSubscribeEvent;
import com.ody.route.domain.DepartureTime;
import com.ody.util.InstantConverter;
import java.time.Instant;
import java.time.LocalDateTime;
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

    private final NotificationRepository notificationRepository;
    private final FcmEventPublisher fcmEventPublisher;

    @Transactional
    public void saveAndSendNotifications2(Meeting meeting, Mate mate, DeviceToken deviceToken) {
        FcmTopic fcmTopic = new FcmTopic(meeting);

        Notification entryNotification = Notification.createEntry(mate, fcmTopic);
        saveAndSendNotification2(entryNotification);

        SubscribeEvent subscribeEvent = new SubscribeEvent(this, deviceToken, fcmTopic);
        fcmEventPublisher.publishSubscribeEvent(subscribeEvent);

        saveAndSendDepartureReminderNotification2(meeting, mate, fcmTopic);
    }

    private void saveAndSendDepartureReminderNotification2(Meeting meeting, Mate mate, FcmTopic fcmTopic) {
        DepartureTime departureTime = new DepartureTime(meeting, mate.getEstimatedMinutes());
        LocalDateTime sendAt = calculateSendAt(departureTime);
        Notification notification = Notification.createDepartureReminder(mate, sendAt, fcmTopic);
        saveAndSendNotification2(notification);
    }

    private LocalDateTime calculateSendAt(DepartureTime departureTime) {
        if (departureTime.isBefore(LocalDateTime.now())) {
            return LocalDateTime.now();
        }
        return departureTime.getValue();
    }

    private void saveAndSendNotification2(Notification notification) {
        Notification savedNotification = notificationRepository.save(notification);
        fcmEventPublisher.schedulePushEvent(this, savedNotification);
    }

    @Transactional
    public void sendNudgeMessage2(Mate requestMate, Mate nudgedMate) {
        Notification nudgeNotification = notificationRepository.save(Notification.createNudge(nudgedMate));
        fcmEventPublisher.publishNudgeEvent(new NudgeEvent(this, requestMate, nudgeNotification));
    }

    public void scheduleNotice(GroupMessage groupMessage, LocalDateTime scheduledTime) {
        fcmEventPublisher.scheduleNoticeEvent(scheduledTime, new NoticeEvent(this, groupMessage));
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
        fcmEventPublisher.publishUnSubscribeEvent(unSubscribeEvent);
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
