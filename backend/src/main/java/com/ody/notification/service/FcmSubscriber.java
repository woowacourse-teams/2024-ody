package com.ody.notification.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.ody.common.exception.OdyServerErrorException;
import com.ody.member.domain.DeviceToken;
import com.ody.notification.domain.FcmTopic;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FcmSubscriber {

    public void subscribeTopic(FcmTopic fcmTopic, DeviceToken deviceToken) {
        try {
            FirebaseMessaging.getInstance()
                    .subscribeToTopic(List.of(deviceToken.getValue()), fcmTopic.getValue());
            log.info("주제 구독에 성공했습니다. -- TOKEN = {}, TOPIC = {}", deviceToken.getValue(), fcmTopic.getValue());
        } catch (Exception exception) {
            log.error("주제 구독에 실패했습니다. -- {}", exception.getMessage());
            throw new OdyServerErrorException("약속 알림 연결에 실패했습니다");
        }
    }

    public void unSubscribeTopic(FcmTopic fcmTopic, DeviceToken deviceToken) {
        try {
            FirebaseMessaging.getInstance()
                    .unsubscribeFromTopic(List.of(deviceToken.getValue()), fcmTopic.getValue());
            log.info("주제 구독 취소에 성공 했습니다. -- TOKEN = {}, TOPIC = {}", deviceToken.getValue(), fcmTopic.getValue());
        } catch (Exception exception) {
            log.error("{} 주제 구독 취소에 실패했습니다. -- {}", fcmTopic.getValue(), exception.getMessage());
            throw new OdyServerErrorException("약속 알림 해제에 실패했습니다");
        }
    }
}
