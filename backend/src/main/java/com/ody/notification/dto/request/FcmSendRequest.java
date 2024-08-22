package com.ody.notification.dto.request;

import com.ody.notification.domain.FcmTopic;
import com.ody.notification.domain.Notification;

public record FcmSendRequest(FcmTopic fcmTopic, Notification notification) {

}
