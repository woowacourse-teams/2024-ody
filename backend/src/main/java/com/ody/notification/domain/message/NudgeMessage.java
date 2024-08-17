package com.ody.notification.domain.message;

import com.google.firebase.messaging.Message;
import com.ody.member.domain.DeviceToken;
import com.ody.notification.domain.Notification;
import lombok.Getter;

@Getter
public class NudgeMessage {

    private final Message message;

    public NudgeMessage(DeviceToken deviceToken, Notification notification) {
        this.message = Message.builder()
                .putData("type", notification.getType().toString())
                .putData("nickname", notification.getMate().getNicknameValue())
                .setToken(deviceToken.getDeviceToken())
                .build();
    }
}
