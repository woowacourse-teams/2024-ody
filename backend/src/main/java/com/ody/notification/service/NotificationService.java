package com.ody.notification.service;

import com.ody.notification.domain.Notification;
import com.ody.notification.dto.response.NotiLogFindResponse;
import com.ody.notification.dto.response.NotificationSaveResponse;
import com.ody.notification.repository.NotificationRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotiLogFindResponse findAllMeetingLogs(Long meetingId) {
        List<Notification> notifications = notificationRepository.findAllMeetingLogsById(meetingId);
        return makeNotiLogResponse(notifications);
    }

    private NotiLogFindResponse makeNotiLogResponse(List<Notification> notifications) {
        List<NotificationSaveResponse> responses = notifications.stream()
                .map(NotificationSaveResponse::new)
                .toList();

        return new NotiLogFindResponse(responses);
    }
}
