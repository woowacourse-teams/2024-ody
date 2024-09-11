package com.ody.notification.domain.message;

import com.google.firebase.messaging.Message;
import com.ody.notification.domain.Notification;
import lombok.Getter;

@Getter
public class TopicPushMessage {

    private final Message message;

    public TopicPushMessage(Notification notification) {
        this.message = Message.builder()
                .putData("type", notification.getType().name())
                .putData("nickname", notification.getMate().getNicknameValue())
                .putData("meetingId", notification.getMate().getMeeting().getId().toString())
                .setTopic(notification.getFcmTopicValue())
                .build();
    }
}
