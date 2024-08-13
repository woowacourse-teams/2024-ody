package com.ody.mate.controller;

import com.ody.common.annotation.AuthMember;
import com.ody.mate.dto.request.MateSaveRequest;
import com.ody.mate.dto.response.MateResponse;
import com.ody.mate.dto.response.MateSaveResponse;
import com.ody.meeting.dto.response.MeetingSaveResponse;
import com.ody.meeting.service.MeetingService;
import com.ody.member.domain.Member;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
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

    @PostMapping("/mates")
    public ResponseEntity<MeetingSaveResponse> save(
            @AuthMember Member member,
            @RequestBody MateSaveRequest mateSaveRequest
    ) {
        MeetingSaveResponse meetingSaveResponse = new MeetingSaveResponse(
                1L,
                "우테코 16조",
                LocalDate.parse("2024-07-15"),
                LocalTime.parse("14:00"),
                "서울 송파구 올림픽로35다길 42",
                "37.515298",
                "127.103113",
                1,
                List.of(new MateResponse("오디")),
                "초대코드"
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(meetingSaveResponse);
    }

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
}
