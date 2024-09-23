package com.ody.notification.domain.message;

import com.google.firebase.messaging.Message;
import com.ody.mate.domain.Mate;
import com.ody.notification.domain.Notification;

public record DirectMessage(Message message) {

    public static DirectMessage createSenderToReceiverMessage(Mate sender, Notification recipientNotification) {
        Message message = Message.builder()
                .putData("type", recipientNotification.getType().name())
                .putData("nickname", sender.getNickname().getValue())
                .putData("meetingId", sender.getMeeting().getId().toString())
                .setToken(recipientNotification.getMate().getMember().getDeviceToken().getValue())
                .build();

        return new DirectMessage(message);
    }

    public static DirectMessage createPrivateMessage(Notification notification) {
        Message message = Message.builder()
                .putData("type", notification.getType().name())
                .putData("nickname", notification.getMate().getNickname().getValue())
                .putData("meetingId", notification.getMate().getMeeting().getId().toString())
                .setToken(notification.getMate().getMember().getDeviceToken().getValue())
                .build();

        return new DirectMessage(message);
    }
}
