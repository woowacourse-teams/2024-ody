package com.ody.mate.controller;

import com.ody.common.annotation.AuthMember;
import com.ody.mate.dto.request.MateSaveRequest;
import com.ody.mate.dto.request.NudgeRequest;
import com.ody.mate.dto.response.MateResponse;
import com.ody.mate.dto.response.MateSaveResponse;
import com.ody.mate.service.MateService;
import com.ody.meeting.service.MeetingService;
import com.ody.member.domain.Member;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MateController implements MateControllerSwagger {

    private final MeetingService meetingService;
    private final MateService mateService;

    @Override
    @PostMapping("/v1/mates")
    public ResponseEntity<MateSaveResponse> saveV1(
            @AuthMember Member member,
            @Valid @RequestBody MateSaveRequest mateSaveRequest
    ) {
        MateSaveResponse mateSaveResponse = meetingService.saveMateAndSendNotifications(mateSaveRequest, member);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mateSaveResponse);
    }

    @Override
    @PostMapping("/v1/mates/nudge")
    public ResponseEntity<Void> nudgeMate(@RequestBody @Valid NudgeRequest nudgeRequest) {
        mateService.nudge(nudgeRequest);
        return ResponseEntity.ok().build();
    }
}
