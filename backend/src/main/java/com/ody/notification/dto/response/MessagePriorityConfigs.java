package com.ody.notification.dto.response;

import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.ApnsConfig;

public record MessagePriorityConfigs(
        AndroidConfig androidConfig,
        ApnsConfig apnsConfig
) {

}
