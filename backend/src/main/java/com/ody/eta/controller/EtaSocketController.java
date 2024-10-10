package com.ody.eta.controller;

import com.ody.common.exception.OdyException;
import com.ody.eta.annotation.WebSocketAuthMember;
import com.ody.eta.dto.request.MateEtaRequest;
import com.ody.eta.service.EtaSocketService;
import com.ody.meeting.dto.response.MateEtaResponsesV2;
import com.ody.member.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
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

    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public ProblemDetail handleOdyException(OdyException exception) {
        log.warn("exception: ", exception);
        return ProblemDetail.forStatusAndDetail(exception.getHttpStatus(), exception.getMessage());
    }

    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public ProblemDetail handleException(Exception exception) {
        log.error("exception: ", exception);
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러");
    }
}
