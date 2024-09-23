package com.ody.notification.domain.message;

import com.google.firebase.messaging.Message;
import com.ody.notification.domain.Notification;

public record GroupMessage(Message message) {

    public static GroupMessage from(Notification notification) {
        Message message = Message.builder()
                .putData("type", notification.getType().name())
                .putData("nickname", notification.getMate().getNickname().getValue())
                .putData("meetingId", notification.getMate().getMeeting().getId().toString())
                .setTopic(notification.getFcmTopic().getValue())
                .build();

        return new GroupMessage(message);
    }
}
