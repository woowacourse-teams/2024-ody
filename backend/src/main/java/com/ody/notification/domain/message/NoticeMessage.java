package com.ody.notification.domain.message;

import com.google.firebase.messaging.Message;
import com.ody.meeting.domain.Meeting;
import com.ody.notification.domain.FcmTopic;
import com.ody.notification.domain.NotificationType;

public record NoticeMessage(Message message) {

    public static NoticeMessage of(Meeting meeting, NotificationType notificationType) {
        FcmTopic fcmTopic = new FcmTopic(meeting);

        Message message = Message.builder()
                .putData("type", notificationType.name())
                .putData("meetingName", meeting.getName())
                .putData("meetingId", meeting.getId().toString())
                .setTopic(fcmTopic.getValue())
                .build();

        return new NoticeMessage(message);
    }
}
