package com.ody.notification.domain.message;

import com.google.firebase.messaging.Message;
import com.ody.notification.domain.FcmTopic;
import com.ody.notification.domain.Notification;
import com.ody.notification.domain.notice.EtaNotice;
import com.ody.notification.domain.notice.EtaSchedulingNotice;
import java.time.format.DateTimeFormatter;

public class GroupMessage extends AbstractMessage {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static GroupMessage create(Notification notification) {
        Message message = Message.builder()
                .putData("type", notification.getType().name())
                .putData("nickname", notification.getMate().getNickname().getValue())
                .putData("meetingId", notification.getMate().getMeeting().getId().toString())
                .setTopic(notification.getFcmTopic().getValue())
                .build();

        return new GroupMessage(message);
    }

    public static GroupMessage create(EtaNotice etaNotice, FcmTopic fcmTopic) {
        Message message = Message.builder()
                .putData("type", etaNotice.getType().name())
                .putData("meetingName", etaNotice.getMeetingName())
                .putData("meetingId", String.valueOf(etaNotice.getMeetingId()))
                .setTopic(fcmTopic.getValue())
                .build();

        return new GroupMessage(message);
    }

    public static GroupMessage create(EtaSchedulingNotice etaSchedulingNotice, FcmTopic fcmTopic) {
        Message message = Message.builder()
                .putData("type", etaSchedulingNotice.getType().name())
                .putData("meetingId", String.valueOf(etaSchedulingNotice.getMeetingId()))
                .putData("meetingTime", etaSchedulingNotice.getMeetingTime().format(FORMATTER))
                .setTopic(fcmTopic.getValue())
                .build();

        return new GroupMessage(message);
    }

    public GroupMessage(Message message) {
        super(message);
    }
}
