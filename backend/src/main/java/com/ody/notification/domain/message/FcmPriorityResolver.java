package com.ody.notification.domain.message;

import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.ody.notification.dto.response.MessagePriorityConfigs;

public class FcmPriorityResolver {

    private static final String FCM_IOS_PRIORITY_HEADER = "apns-priority";

    public static MessagePriorityConfigs resolve(MessagePriority messagePriority) {
        AndroidConfig androidConfig = buildAndroidConfig(messagePriority);
        ApnsConfig iosConfig = buildIosConfig(messagePriority);
        return new MessagePriorityConfigs(androidConfig, iosConfig);
    }

    private static AndroidConfig buildAndroidConfig(MessagePriority messagePriority) {
        return AndroidConfig.builder()
                .setPriority(messagePriority.getAndroidPriority())
                .build();
    }

    private static ApnsConfig buildIosConfig(MessagePriority messagePriority) {
        return ApnsConfig.builder()
                .putHeader(FCM_IOS_PRIORITY_HEADER, messagePriority.getIosPriority())
                .setAps(Aps.builder().setContentAvailable(true).build())
                .build();
    }
}
