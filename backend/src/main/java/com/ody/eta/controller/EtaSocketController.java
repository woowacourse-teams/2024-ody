package com.ody.eta.controller;

import com.ody.eta.annotation.WebSocketAuthMember;
import com.ody.eta.dto.request.MateEtaRequest;
import com.ody.mate.service.MateService;
import com.ody.meeting.dto.response.MateEtaResponsesV2;
import com.ody.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EtaSocketController {

    private final MateService mateService;

    @MessageMapping("/open/{meetingId}")
    public void open() {
    }

    @MessageMapping("etas/{meetingId}")
    @SendTo("topic/etas/{meetingId}")
    public MateEtaResponsesV2 etaUpdate(
            @DestinationVariable String meetingId,
            @WebSocketAuthMember Member member,
            @Payload MateEtaRequest etaRequest
    ) {
        return mateService.findAllMateEtas(etaRequest, Long.valueOf(meetingId), member);
    }
}
