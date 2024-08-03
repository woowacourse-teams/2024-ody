package com.ody.mate.controller;

import com.ody.common.annotation.AuthMember;
import com.ody.mate.dto.request.MateSaveRequest;
import com.ody.mate.service.MateService;
import com.ody.meeting.dto.response.MeetingSaveResponse;
import com.ody.meeting.service.MeetingService;
import com.ody.member.domain.Member;
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

    @PostMapping("/mates")
    public ResponseEntity<MeetingSaveResponse> save(
            @AuthMember Member member,
            @RequestBody MateSaveRequest mateSaveRequest
    ) {
        MeetingSaveResponse meetingSaveResponse = meetingService.findAndSendNotifications(mateSaveRequest, member);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(meetingSaveResponse);
    }

    @Override
    @PostMapping("/v1/mates")
    public ResponseEntity<MeetingSaveResponse> saveV1(
            @AuthMember Member member,
            @RequestBody MateSaveRequest mateSaveRequest
    ) {
        MeetingSaveResponse meetingSaveResponse = mateService.saveAndSendNotifications(mateSaveRequest, member);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(meetingSaveResponse);
    }
}
