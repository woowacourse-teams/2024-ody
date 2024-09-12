package com.ody.eta.service;

import com.ody.eta.dto.request.MateEtaRequest;
import com.ody.mate.service.MateService;
import com.ody.meeting.domain.Meeting;
import com.ody.meeting.dto.response.MateEtaResponsesV2;
import com.ody.meeting.service.MeetingService;
import com.ody.member.domain.Member;
import com.ody.util.TimeUtil;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EtaSocketService {

    private static final ZoneOffset KST_OFFSET = ZoneOffset.ofHours(9);
    private static final Map<Long, LocalDateTime> LATEST_TRIGGER_TIME_CACHE = new ConcurrentHashMap<>();
    private static final Map<Long, LocalDateTime> MEETING_TIME_CACHE = new ConcurrentHashMap<>();

    private final MeetingService meetingService;
    private final MateService mateService;
    private final TaskScheduler taskScheduler;
    private final SimpMessagingTemplate template;

    public void open(Long meetingId) {
        if (!MEETING_TIME_CACHE.containsKey(meetingId)) {
            Meeting meeting = meetingService.findById(meetingId);
            MEETING_TIME_CACHE.put(meetingId, meeting.getMeetingTime());
        }
        scheduleTrigger(meetingId, LocalDateTime.now().plusSeconds(1));
    }

    public MateEtaResponsesV2 etaUpdate(Long meetingId, Member member, MateEtaRequest etaRequest) {
        if (isOverMeetingTime(meetingId)) {
            log.info("--- websocket disconnect ! - {}", meetingId);
            template.convertAndSend("/topic/disconnect/" + meetingId, "");
        } else if (isTimeToSchedule(meetingId)) {
            scheduleTrigger(meetingId, LocalDateTime.now().plusSeconds(10));
        }
        return mateService.findAllMateEtas(etaRequest, meetingId, member);
    }

    private boolean isOverMeetingTime(Long meetingId) {
        LocalDateTime meetingTime = MEETING_TIME_CACHE.get(meetingId);
        LocalDateTime lastTriggerTime = meetingTime.plusMinutes(1L);
        return TimeUtil.nowWithTrim().isAfter(lastTriggerTime);
    }

    private boolean isTimeToSchedule(Long meetingId) {
        LocalDateTime lastTriggerTime = LATEST_TRIGGER_TIME_CACHE.get(meetingId);
        Duration duration = Duration.between(lastTriggerTime, LocalDateTime.now());
        return duration.toSeconds() >= 10;
    }

    private void scheduleTrigger(Long meetingId, LocalDateTime triggerTime) {
        Instant startTime = triggerTime.toInstant(KST_OFFSET);
        taskScheduler.schedule(
                () -> template.convertAndSend("topic/coordinates/" + meetingId, ""), startTime
        );
        LATEST_TRIGGER_TIME_CACHE.put(meetingId, LocalDateTime.now());
        log.info("--- schedule 예약 완료 ! - {}, {}", meetingId, triggerTime);
    }
}
