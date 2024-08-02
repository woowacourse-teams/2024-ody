package com.ody.notification.dto.request;

import com.ody.meeting.domain.Meeting;
import com.ody.notification.domain.Notification;

public record FcmSendRequest(String topic, Notification notification) {

    public FcmSendRequest(Meeting meeting, Notification notification) {
        this(meeting.getId().toString(), notification);
    }
}
