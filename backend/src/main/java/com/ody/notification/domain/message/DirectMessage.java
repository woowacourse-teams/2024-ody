package com.ody.notification.domain.message;

import com.google.firebase.messaging.Message;
import com.ody.mate.domain.Mate;
import com.ody.member.domain.DeviceToken;
import com.ody.notification.domain.Notification;
import com.ody.notification.domain.trigger.EtaTrigger;
import java.time.format.DateTimeFormatter;

public class DirectMessage extends AbstractMessage {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static DirectMessage createMessageToOther(Mate sender, Notification recipientNotification) {
        Message message = Message.builder()
                .putData("type", recipientNotification.getType().name())
                .putData("nickname", sender.getNickname().getValue())
                .putData("meetingId", sender.getMeeting().getId().toString())
                .setToken(recipientNotification.getMate().getMember().getDeviceToken().getValue())
                .build();

        return new DirectMessage(message);
    }

    public static DirectMessage create(EtaTrigger trigger, DeviceToken deviceToken) {
        Message message = Message.builder()
                .putData("type", trigger.getType().name())
                .putData("meetingId", String.valueOf(trigger.getMeetingId()))
                .putData("meetingTime", trigger.getMeetingTime().format(FORMATTER))
                .setToken(deviceToken.getValue())
                .build();

        return new DirectMessage(message);
    }

    public DirectMessage(Message message) {
        super(message);
    }
}
