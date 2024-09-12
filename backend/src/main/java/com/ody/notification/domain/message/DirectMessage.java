package com.ody.notification.domain.message;

import com.google.firebase.messaging.Message;
import com.ody.mate.domain.Mate;
import com.ody.notification.domain.Notification;
import lombok.Getter;

@Getter
public class DirectMessage {

    private final Message message;

    public DirectMessage(Mate requestMate, Notification recipientNotification) {
        this.message = Message.builder()
                .putData("type", recipientNotification.getType().name())
                .putData("nickname", requestMate.getNickname())
                .putData("meetingId", requestMate.getMeeting().getId().toString())
                .setToken(recipientNotification.getMate().getMember().getDeviceToken().getValue())
                .build();
    }

    public DirectMessage(Notification recipientNotification) {
        this.message = Message.builder()
                .putData("type", recipientNotification.getType().name())
                .putData("nickname", recipientNotification.getMate().getNickname())
                .putData("meetingId", recipientNotification.getMate().getMeeting().getId().toString())
                .setToken(recipientNotification.getMate().getMember().getDeviceToken().getValue())
                .build();
    }
}
