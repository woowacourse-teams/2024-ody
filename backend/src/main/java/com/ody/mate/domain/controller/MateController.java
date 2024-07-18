package com.ody.mate.domain.controller;

import com.ody.mate.domain.dto.MateSaveRequest;
import com.ody.meeting.dto.MateResponse;
import com.ody.meeting.dto.MeetingSaveResponse;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MateController implements MateControllerSwagger {

    @Override
    @PostMapping("/mates")
    public ResponseEntity<MeetingSaveResponse> save(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String fcmToken,
            MateSaveRequest mateSaveRequest
    ) {
        return ResponseEntity.ok(
                new MeetingSaveResponse(1L, "우테코 16조", LocalDate.parse("2024-07-15"), LocalTime.parse("14:00"),
                        "서울 송파구 올림픽로35다길 42", "37.515298", "127.103113", 2,
                        List.of(new MateResponse("오디"), new MateResponse("제리")), "초대코드"));
    }
}
