package com.ody.notification.dto.request;

import com.ody.notification.domain.Notification;

public record FcmSendRequest(Notification notification) {

}
