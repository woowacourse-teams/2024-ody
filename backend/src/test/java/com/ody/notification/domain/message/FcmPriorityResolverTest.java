package com.ody.notification.domain.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.ApnsConfig;
import com.ody.notification.dto.response.MessagePriorityConfigs;
import java.lang.reflect.Field;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class FcmPriorityResolverTest {

    @DisplayName("우선순위에 따른 안드로이드 & ios 설정을 구성한다")
    @ParameterizedTest
    @EnumSource(MessagePriority.class)
    void resolve(MessagePriority priority) throws Exception {
        MessagePriorityConfigs configs = FcmPriorityResolver.resolve(priority);
        String androidPriority = extractAndroidPriority(configs.androidConfig());
        String iosPriority = extractIosPriority(configs.apnsConfig());

        assertAll(
                () -> assertThat(androidPriority.toUpperCase()).contains(priority.getAndroidPriority().name()),
                () -> assertThat(iosPriority).contains(priority.getIosPriority())
        );
    }

    private String extractAndroidPriority(AndroidConfig config) throws Exception {
        Field priorityField = AndroidConfig.class.getDeclaredField("priority");
        priorityField.setAccessible(true);
        Object priorityValue = priorityField.get(config);
        priorityField.setAccessible(true);
        return (String) priorityValue;
    }

    public static String extractIosPriority(ApnsConfig config) throws Exception {
        Field headersField = ApnsConfig.class.getDeclaredField("headers");
        headersField.setAccessible(true);
        Map<String, String> headers = (Map<String, String>) headersField.get(config);
        headersField.setAccessible(false);
        return headers.get("apns-priority");
    }
}
