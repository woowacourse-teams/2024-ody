package com.ody.notification.dto.request;

import com.ody.meeting.domain.Meeting;
import com.ody.notification.domain.Notification;
import java.time.LocalDateTime;

public record FcmSendRequest(
        String topic,
        Long notificationId,
        LocalDateTime sendAt
) {

    public FcmSendRequest(Meeting meeting, Notification notification) {
        this(meeting.getId().toString(), notification.getId(), notification.getSendAt());
    }
}
