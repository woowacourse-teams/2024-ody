package com.ody.eta.service;

import com.ody.eta.dto.request.MateEtaRequest;
import com.ody.mate.service.MateService;
import com.ody.meeting.domain.Meeting;
import com.ody.meeting.dto.response.MateEtaResponsesV2;
import com.ody.meeting.service.MeetingService;
import com.ody.member.domain.Member;
import com.ody.util.TimeUtil;
import com.ody.util.cache.Cache;
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

    private final Cache<Long, LocalDateTime> lastTriggerTimeCache;;
    private final Cache<Long, LocalDateTime> meetingTimeCache;
    private final MeetingService meetingService;
    private final MateService mateService;
    private final SocketMessageSender socketMessageSender;

    public void open(Long meetingId) {
        if (!meetingTimeCache.exists(meetingId)) {
            Meeting meeting = meetingService.findById(meetingId);
            meetingTimeCache.put(meetingId, meeting.getMeetingTime());
        }
        reserveLocationTrigger(meetingId, 1L);
    }

    private void reserveLocationTrigger(long meetingId, long timeGap) {
        LocalDateTime triggerTime = LocalDateTime.now().plusSeconds(timeGap);
        socketMessageSender.reserveMessage(locationTrigger(meetingId), triggerTime);
        lastTriggerTimeCache.put(meetingId, LocalDateTime.now());
    }

    public MateEtaResponsesV2 etaUpdate(Long meetingId, Member member, MateEtaRequest etaRequest) {
        if (isOverMeetingTime(meetingId)) {
            log.info("--- websocket disconnect ! - {}", meetingId);
            socketMessageSender.sendMessage(disconnectTrigger(meetingId));
        } else if (isTimeToSchedule(meetingId)) {
            reserveLocationTrigger(meetingId, 10L);
        }
        return mateService.findAllMateEtas(etaRequest, meetingId, member);
    }

    private boolean isOverMeetingTime(Long meetingId) {
        LocalDateTime meetingTime = meetingTimeCache.get(meetingId);
        LocalDateTime lastTriggerTime = meetingTime.plusMinutes(1L);
        return TimeUtil.nowWithTrim().isAfter(lastTriggerTime);
    }

    private boolean isTimeToSchedule(Long meetingId) {
        LocalDateTime lastTriggerTime = lastTriggerTimeCache.get(meetingId);
        Duration duration = Duration.between(lastTriggerTime, LocalDateTime.now());
        return duration.toSeconds() >= 10;
    }

    private String locationTrigger(Long meetingId){
        return "/topic/coordinates/" + meetingId;
    }

    private String disconnectTrigger(Long meetingId){
        return "/topic/disconnect/" + meetingId;
    }
}
