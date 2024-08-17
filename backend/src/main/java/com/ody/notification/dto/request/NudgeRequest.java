package com.ody.notification.dto.request;

import com.ody.mate.domain.Mate;
import com.ody.member.domain.DeviceToken;
import com.ody.notification.domain.Notification;

public record NudgeRequest(DeviceToken deviceToken, Notification notification) {

    public NudgeRequest(Mate mate, Notification notification){
        this(mate.getMember().getDeviceToken(), notification);
    }
}
