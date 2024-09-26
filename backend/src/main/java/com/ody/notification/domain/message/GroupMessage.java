package com.ody.notification.domain.message;

import com.google.firebase.messaging.Message;
import com.ody.meeting.domain.Meeting;
import com.ody.notification.domain.FcmTopic;
import com.ody.notification.domain.Notification;
import com.ody.notification.domain.NotificationType;

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

    public static GroupMessage createMeetingNotice(Meeting meeting, NotificationType notificationType) {
        FcmTopic fcmTopic = new FcmTopic(meeting);
        Message message = Message.builder()
                .putData("type", notificationType.name())
                .putData("meetingName", meeting.getName())
                .putData("meetingId", meeting.getId().toString())
                .setTopic(fcmTopic.getValue())
                .build();

        return new GroupMessage(message);
    }
}
