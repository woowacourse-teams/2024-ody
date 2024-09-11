package com.ody.notification.domain.message;

import com.google.firebase.messaging.Message;
import com.ody.member.domain.DeviceToken;
import com.ody.notification.domain.NotificationType;
import lombok.Getter;

@Getter
public class NudgeMessage {

    private final Message message;

    public NudgeMessage(DeviceToken nudgeMateDeviceToken, String requestMateNickName, String meetingId) {
        this.message = Message.builder()
                .putData("type", NotificationType.NUDGE.name())
                .putData("nickname", requestMateNickName)
                .putData("meetingId", meetingId)
                .setToken(nudgeMateDeviceToken.getValue())
                .build();
    }
}
