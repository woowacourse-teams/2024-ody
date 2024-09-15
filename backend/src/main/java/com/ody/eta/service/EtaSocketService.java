package com.ody.eta.service;

import com.ody.eta.domain.WebSocketTrigger;
import com.ody.eta.dto.request.MateEtaRequest;
import com.ody.mate.service.MateService;
import com.ody.meeting.domain.Meeting;
import com.ody.meeting.dto.response.MateEtaResponsesV2;
import com.ody.meeting.service.MeetingService;
import com.ody.member.domain.Member;
import com.ody.util.TimeCache;
import com.ody.util.TimeUtil;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EtaSocketService {

    private static final int LOCATION_TRIGGER_CALL_MINUTE_GAP = 10;

    private final TimeCache lastTriggerTimeCache;;
    private final TimeCache meetingTimeCache;
    private final MeetingService meetingService;
    private final MateService mateService;
    private final SocketMessageSender socketMessageSender;

    public synchronized void open(Long meetingId) {
        if (meetingTimeCache.exists(meetingId)) {
            return;
        }

        Meeting meeting = meetingService.findById(meetingId);
        meetingTimeCache.put(meetingId, meeting.getMeetingTime());
        reserveLocationTrigger(meetingId, 1L);
    }

    private void reserveLocationTrigger(long meetingId, long timeGap) {
        LocalDateTime triggerTime = LocalDateTime.now().plusSeconds(timeGap);
        socketMessageSender.reserveMessage(WebSocketTrigger.LOCATION.trigger(meetingId), triggerTime);
        lastTriggerTimeCache.put(meetingId, LocalDateTime.now());
    }

    public synchronized MateEtaResponsesV2 etaUpdate(Long meetingId, Member member, MateEtaRequest etaRequest) {
        if (isOverMeetingTime(meetingId)) {
            log.info("--- websocket disconnect ! - {}", meetingId);
            socketMessageSender.sendMessage(WebSocketTrigger.DISCONNECT.trigger(meetingId));
        } else if (isTimeToSchedule(meetingId)) {
            reserveLocationTrigger(meetingId, LOCATION_TRIGGER_CALL_MINUTE_GAP);
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
        return duration.toSeconds() >= LOCATION_TRIGGER_CALL_MINUTE_GAP;
    }
}
