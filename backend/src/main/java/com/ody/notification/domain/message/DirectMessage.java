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
                .putData("nickname", requestMate.getNicknameValue())
                .putData("meetingId", requestMate.getMeetingId().toString())
                .setToken(recipientNotification.getMateDeviceToken().getValue())
                .build();
    }

    public DirectMessage(Notification recipientNotification) {
        this.message = Message.builder()
                .putData("type", recipientNotification.getType().name())
                .putData("nickname", recipientNotification.getMateNicknameValue())
                .putData("meetingId", recipientNotification.getMeetingId().toString())
                .setToken(recipientNotification.getMateDeviceToken().getValue())
                .build();
    }
}
