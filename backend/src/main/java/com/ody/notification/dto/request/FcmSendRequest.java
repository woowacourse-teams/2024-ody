package com.ody.notification.dto.request;

import java.time.LocalDateTime;

public record FcmSendRequest(
        String topic,
        Long notificationId,
        LocalDateTime sendAt
) {

}
