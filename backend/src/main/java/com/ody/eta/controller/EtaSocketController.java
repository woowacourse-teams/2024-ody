package com.ody.eta.controller;

import com.ody.eta.annotation.WebSocketAuthMember;
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
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class EtaSocketController {

    private static final ZoneOffset KST_OFFSET = ZoneOffset.ofHours(9);
    private static final Map<Long, LocalDateTime> LATEST_TRIGGER_TIME_CACHE = new ConcurrentHashMap<>();
    private static final Map<Long, LocalDateTime> MEETING_TIME_CACHE = new ConcurrentHashMap<>();

    private final MateService mateService;
    private final TaskScheduler taskScheduler;
    private final SimpMessagingTemplate template;
    private final MeetingService meetingService;

    @MessageMapping("/open/{meetingId}")
    public void open(@DestinationVariable Long meetingId) {
        log.info("---- websocket open !! - {}", meetingId);
        if (!MEETING_TIME_CACHE.containsKey(meetingId)) {
            log.info("meeting 캐싱 !!");
            Meeting meeting = meetingService.findById(meetingId);
            MEETING_TIME_CACHE.put(meetingId, meeting.getMeetingTime());
        }
        scheduleTrigger(meetingId, LocalDateTime.now().plusSeconds(1));
    }

    @MessageMapping("/etas/{meetingId}")
    @SendTo("/topic/etas/{meetingId}")
    public MateEtaResponsesV2 etaUpdate(
            @DestinationVariable Long meetingId,
            @WebSocketAuthMember Member member,
            @Payload MateEtaRequest etaRequest
    ) {
        log.info("---- eta 좌표 왔다 !! - {}, {}, {}", meetingId, member, etaRequest);
        if (isOverMeetingTime(meetingId)) {
            log.info("---- websocket disconnect !! - {}", meetingId);
            template.convertAndSend("/topic/disconnect/" + meetingId);
        } else if (isTimeToSchedule(meetingId)) {
            log.info("------schedule trigger");
            scheduleTrigger(meetingId, LocalDateTime.now().plusSeconds(10));
        }
        log.info("---- eta 목록 반환");
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
        taskScheduler.schedule(() -> template.convertAndSend("topic/coordinates/" + meetingId), startTime);
        LATEST_TRIGGER_TIME_CACHE.put(meetingId, LocalDateTime.now());
        log.info("schedule 예약 완료 !! - {}, {}", meetingId, triggerTime);
    }
}
