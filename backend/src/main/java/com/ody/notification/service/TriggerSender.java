package com.ody.notification.service;

import com.ody.eta.domain.EtaTriggerTime;
import com.ody.member.domain.DeviceToken;
import com.ody.notification.domain.FcmTopic;
import com.ody.notification.domain.message.DirectMessage;
import com.ody.notification.domain.message.GroupMessage;
import com.ody.notification.domain.trigger.EtaTrigger;
import com.ody.notification.service.event.TriggerEvent;
import com.ody.util.InstantConverter;
import com.ody.util.TimeUtil;
import java.time.Instant;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TriggerSender {

    private final FcmEventPublisher fcmEventPublisher;
    private final TaskScheduler taskScheduler;

    public void sendGroupTrigger(EtaTrigger trigger, FcmTopic fcmTopic) {
        GroupMessage groupMessage = GroupMessage.create(trigger, fcmTopic);
        TriggerEvent<GroupMessage> triggerEvent = new TriggerEvent<>(this, trigger, groupMessage);
        fcmEventPublisher.publish(triggerEvent);
    }

    public void sendDirectTrigger(EtaTrigger trigger, DeviceToken deviceToken) {
        DirectMessage directMessage = DirectMessage.create(trigger, deviceToken);
        TriggerEvent<DirectMessage> triggerEvent = new TriggerEvent<>(this, trigger, directMessage);
        fcmEventPublisher.publish(triggerEvent);
    }

    public void sendNowOrScheduleLater(EtaTriggerTime triggerTime, Runnable task) {
        LocalDateTime time = triggerTime.getTime();
        if (TimeUtil.isUpcoming(time)) {
            Instant startTime = InstantConverter.kstToInstant(time);
            taskScheduler.schedule(task, startTime);
            return;
        }
        task.run();
    }
}
