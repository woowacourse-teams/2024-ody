package com.ody.notification.dto.request;

import com.ody.notification.domain.NotificationType;

import java.time.LocalDateTime;

public record FcmSendRequest(
        String topic,
        Long notificationId,
        LocalDateTime sendAt
) {

}
