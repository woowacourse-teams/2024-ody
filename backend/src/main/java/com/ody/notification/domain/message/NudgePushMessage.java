package com.ody.notification.domain.message;

import com.google.firebase.messaging.Message;
import com.ody.mate.domain.Mate;
import com.ody.notification.domain.Notification;
import lombok.Getter;

@Getter
public class NudgePushMessage {

    private final Message message;

    public NudgePushMessage(Mate requestMate, Notification recipientNotification) {
        this.message = Message.builder()
                .putData("type", recipientNotification.getType().name())
                .putData("nickname", requestMate.getNicknameValue())
                .putData("meetingId", requestMate.getMeeting().getId().toString())
                .setToken(recipientNotification.getMateDeviceToken().getValue())
                .build();
    }
}
