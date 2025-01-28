package com.ody.eta.service;

import com.ody.eta.domain.EtaSchedulingKey;
import com.ody.eta.repository.EtaSchedulingRedisTemplate;
import com.ody.mate.domain.Mate;
import com.ody.meeting.domain.Meeting;
import com.ody.meeting.repository.MeetingRepository;
import com.ody.notification.domain.FcmTopic;
import com.ody.notification.domain.message.DirectMessage;
import com.ody.notification.domain.message.GroupMessage;
import com.ody.notification.domain.notice.EtaSchedulingNotice;
import com.ody.notification.service.NoticeService;
import com.ody.util.InstantConverter;
import com.ody.util.TimeUtil;
import java.time.Instant;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EtaSchedulingService {

    private static final long NOTICE_TIME_DEFER = 30L;

    private final TaskScheduler taskScheduler;
    private final MeetingRepository meetingRepository;
    private final EtaSchedulingRedisTemplate schedulingCacheService;
    private final NoticeService noticeService;

    public void sendNotice(Meeting meeting) {
        LocalDateTime noticeTime = meeting.getMeetingTime().minusMinutes(NOTICE_TIME_DEFER);
        EtaSchedulingNotice notice = new EtaSchedulingNotice(noticeTime, meeting.getId(), meeting.getMeetingTime());
        sendNowOrScheduleLater(noticeTime, () -> noticeGroupMessageAndCache(notice));
    }

    private void sendNowOrScheduleLater(LocalDateTime noticeTime, Runnable task) {
        if (isUpcoming(noticeTime)) {
            Instant startTime = InstantConverter.kstToInstant(noticeTime);
            taskScheduler.schedule(task, startTime);
            return;
        }
        task.run();
    }

    private boolean isUpcoming(LocalDateTime dateTime) {
        return dateTime.isAfter(TimeUtil.nowWithTrim());
    }

    private void noticeGroupMessageAndCache(EtaSchedulingNotice notice) {
        meetingRepository.findById(notice.getMeetingId())
                .ifPresent(meeting -> {
                    GroupMessage groupMessage = GroupMessage.create(notice, new FcmTopic(meeting));
                    noticeService.send(notice, groupMessage);
                    schedulingCacheService.addAll(meeting);
                });
    }

    public void sendFallbackNotice(EtaSchedulingKey etaSchedulingKey) {
        if (isPast(etaSchedulingKey.meetingDateTime())) {
            return;
        }
        EtaSchedulingNotice notice = new EtaSchedulingNotice(TimeUtil.nowWithTrim(), etaSchedulingKey);
        DirectMessage directMessage = DirectMessage.create(notice, etaSchedulingKey.deviceToken());
        noticeService.send(notice, directMessage);
        schedulingCacheService.add(etaSchedulingKey);
    }

    private boolean isPast(LocalDateTime dateTime) {
        return TimeUtil.nowWithTrim().isAfter(dateTime);
    }

    public void updateCache(Mate mate) {
        schedulingCacheService.add(EtaSchedulingKey.from(mate));
    }
}
