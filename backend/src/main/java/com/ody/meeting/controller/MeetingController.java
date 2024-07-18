package com.ody.meeting.controller;

import com.ody.meeting.dto.MateResponse;
import com.ody.meeting.dto.MeetingSaveRequest;
import com.ody.meeting.dto.MeetingSaveResponse;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    public ResponseEntity<MeetingSaveResponse> save(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String fcmToken,
            @RequestBody MeetingSaveRequest meetingSaveRequest
    ) {
        return ResponseEntity.created(URI.create("/meetings"))
                .body(new MeetingSaveResponse(1L, "우테코 16조", LocalDate.parse("2024-07-15"), LocalTime.parse("14:00"),
                        "서울 송파구 올림픽로35다길 42", "37.515298", "127.103113", 1, List.of(new MateResponse("오디")), "초대코드"));
    }

    @Override
    @GetMapping("/invite-codes/{inviteCode}/validate")
    public ResponseEntity<Void> validateInviteCode(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String fcmToken,
            @PathVariable String inviteCode
    ) {
        return ResponseEntity.ok().build();
    }
}
