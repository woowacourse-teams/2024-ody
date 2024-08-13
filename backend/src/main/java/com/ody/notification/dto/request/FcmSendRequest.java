package com.ody.notification.dto.request;

import com.ody.meeting.domain.Meeting;
import com.ody.notification.domain.FcmTopic;
import com.ody.notification.domain.Notification;

public record FcmSendRequest(FcmTopic fcmTopic, Notification notification) {

    public FcmSendRequest(Meeting meeting, Notification notification) {
        this(meeting.buildFcmTopic(), notification);
    }
}
