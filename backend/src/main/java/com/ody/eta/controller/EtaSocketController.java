package com.ody.eta.controller;

import com.ody.eta.annotation.WebSocketAuthMember;
import com.ody.eta.dto.request.MateEtaRequest;
import com.ody.eta.service.EtaSocketService;
import com.ody.meeting.dto.response.MateEtaResponsesV2;
import com.ody.member.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class EtaSocketController {

    private final EtaSocketService etaSocketService;

    @MessageMapping("/open/{meetingId}")
    public void open(@DestinationVariable Long meetingId) {
        log.info("--- websocket open ! - {}", meetingId);
        etaSocketService.open(meetingId);
    }

    @MessageMapping("/etas/{meetingId}")
    @SendTo("/topic/etas/{meetingId}")
    public MateEtaResponsesV2 etaUpdate(
            @DestinationVariable Long meetingId,
            @WebSocketAuthMember Member member,
            @Payload MateEtaRequest etaRequest
    ) {
        log.info("--- etaUpdate 호출 ! - {}, {}, {}", meetingId, member, etaRequest);
        return etaSocketService.etaUpdate(meetingId, member, etaRequest);
    }
}
