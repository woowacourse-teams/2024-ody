package com.ody.notification.domain.message;

import com.google.firebase.messaging.Message;
import com.ody.notification.domain.Notification;
import lombok.Getter;

@Getter
public class PushMessage {

    private final Message message;

    public PushMessage(String topic, Notification notification){
        this.message= Message.builder()
                .putData("type", notification.getType().toString())
                .putData("nickname", notification.getMate().getNicknameValue())
                .setTopic(topic)
                .build();
    }
}
