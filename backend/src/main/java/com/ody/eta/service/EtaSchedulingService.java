package com.ody.eta.service;

import com.ody.common.aop.EnableDeletedFilter;
import com.ody.eta.domain.EtaSchedulingKey;
import com.ody.mate.domain.Mate;
import com.ody.meeting.domain.Meeting;
import com.ody.meeting.repository.MeetingRepository;
import com.ody.notification.domain.trigger.MateEtaTrigger;
import com.ody.notification.domain.trigger.MeetingEtaTrigger;
import com.ody.notification.service.TriggerSender;
import com.ody.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@EnableDeletedFilter
@RequiredArgsConstructor
public class EtaSchedulingService {

    private final MeetingRepository meetingRepository;
    private final EtaSchedulingRedisTemplate etaSchedulingRedisTemplate;
    private final TriggerSender triggerSender;

    public void sendTrigger(Meeting meeting) {
        if (TimeUtil.isPast(meeting.getMeetingTime())) {
            return;
        }
        MeetingEtaTrigger trigger = MeetingEtaTrigger.from(meeting);
        triggerSender.sendNowOrScheduleLater(trigger.getTime(), () -> sendEtaSchedulingNoticeAndCache(trigger));
    }

    private void sendEtaSchedulingNoticeAndCache(MeetingEtaTrigger trigger) {
        meetingRepository.findById(trigger.getMeetingId())
                .ifPresent(meeting -> {
                    triggerSender.sendGroupTrigger(trigger, trigger.getFcmTopic());
                    etaSchedulingRedisTemplate.addAll(meeting);
                });
    }

    public void sendFallbackNotice(EtaSchedulingKey etaSchedulingKey) {
        if (TimeUtil.isPast(etaSchedulingKey.meetingDateTime())) {
            return;
        }
        MateEtaTrigger fallBackTrigger = MateEtaTrigger.from(etaSchedulingKey);
        triggerSender.sendDirectTrigger(fallBackTrigger, fallBackTrigger.getDeviceToken());
        etaSchedulingRedisTemplate.add(etaSchedulingKey);
    }

    public void updateCache(Mate mate) {
        etaSchedulingRedisTemplate.add(EtaSchedulingKey.from(mate));
    }

    public void deleteCache(Mate mate) {
        etaSchedulingRedisTemplate.delete(EtaSchedulingKey.from(mate));
    }
}
