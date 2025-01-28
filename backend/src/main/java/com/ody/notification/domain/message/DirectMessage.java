package com.ody.notification.domain.message;

import com.google.firebase.messaging.Message;
import com.ody.mate.domain.Mate;
import com.ody.notification.domain.Notification;
import com.ody.notification.domain.notice.EtaSchedulingNotice;
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

    public static DirectMessage createMessageToSelf(Notification notification) {
        Message message = Message.builder()
                .putData("type", notification.getType().name())
                .putData("nickname", notification.getMate().getNickname().getValue())
                .putData("meetingId", notification.getMate().getMeeting().getId().toString())
                .setToken(notification.getMate().getMember().getDeviceToken().getValue())
                .build();

        return new DirectMessage(message);
    }

    public static DirectMessage create(EtaSchedulingNotice etaSchedulingNotice, String deviceToken) {
        Message message = Message.builder()
                .putData("type", etaSchedulingNotice.getType().name())
                .putData("meetingId", String.valueOf(etaSchedulingNotice.getMeetingId()))
                .putData("meetingTime", etaSchedulingNotice.getMeetingTime().format(FORMATTER))
                .setToken(deviceToken)
                .build();

        return new DirectMessage(message);
    }

    public DirectMessage(Message message) {
        super(message);
    }
}
