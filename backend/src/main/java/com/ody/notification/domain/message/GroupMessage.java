package com.ody.notification.domain.message;

import com.google.firebase.messaging.Message;
import com.ody.notification.domain.Notification;
import lombok.Getter;

@Getter
public class GroupMessage {

    private final Message message;

    public GroupMessage(Notification notification) {
        this.message = Message.builder()
                .putData("type", notification.getType().name())
                .putData("nickname", notification.getMateNicknameValue())
                .putData("meetingId", notification.getMeetingId().toString())
                .setTopic(notification.getFcmTopicValue())
                .build();
    }
}
