package com.ody.meeting.controller;

import com.ody.meeting.dto.MateResponse;
import com.ody.meeting.dto.MeetingRequest;
import com.ody.meeting.dto.MeetingResponse;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MeetingController implements MeetingControllerSwagger {

    @Override
    @PostMapping("/meetings")
    public ResponseEntity<MeetingResponse> save(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String fcmToken,
            @RequestBody MeetingRequest meetingRequest
    ) {
        return ResponseEntity.ok(
                new MeetingResponse(1L, "우테코 16조", LocalDate.parse("2024-07-15"), LocalTime.parse("14:00"),
                        "서울 송파구 올림픽로35다길 42", "37.515298", "127.103113", 1, List.of(new MateResponse("오디")), "초대코드"));
    }
}
