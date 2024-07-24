package com.ody.meeting.controller;

import com.ody.meeting.dto.request.MeetingSaveRequest;
import com.ody.meeting.dto.response.MateResponse;
import com.ody.meeting.dto.response.MeetingSaveResponse;
import com.ody.meeting.dto.response.MeetingSaveResponses;
import com.ody.notification.domain.Notification;
import com.ody.notification.dto.response.NotiLogFindResponses;
import com.ody.notification.service.NotificationService;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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

    private final NotificationService notificationService;

    @Override
    @GetMapping("/meetings/me")
    public ResponseEntity<MeetingSaveResponses> findMine(@RequestHeader(HttpHeaders.AUTHORIZATION) String fcmToken) {
        return ResponseEntity.ok(new MeetingSaveResponses(
                        List.of(
                                new MeetingSaveResponse(1L, "우테코 16조", LocalDate.parse("2024-07-15"), LocalTime.parse("14:00"),
                                        "서울 송파구 올림픽로35다길 42", "37.515298", "127.103113", 2,
                                        List.of(new MateResponse("조조"), new MateResponse("제리")), "초대코드"),
                                new MeetingSaveResponse(2L, "우테코 15조", LocalDate.parse("2024-07-17"), LocalTime.parse("14:00"),
                                        "서울 송파구 올림픽로35다길 42", "37.515298", "127.103113", 2,
                                        List.of(new MateResponse("카키"), new MateResponse("제리")), "초대코드")
                        )
                )
        );
    }

    @Override
    @GetMapping("/meetings/{meetingId}/noti-log")
    public ResponseEntity<NotiLogFindResponses> findAllMeetingLogs(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String fcmToken,
            @PathVariable Long meetingId
    ) {

        List<Notification> notifications = notificationService.findAllMeetingLogs(meetingId);
        NotiLogFindResponses response = NotiLogFindResponses.from(notifications);
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/invite-codes/{inviteCode}/validate")
    public ResponseEntity<Void> validateInviteCode(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String fcmToken,
            @PathVariable String inviteCode
    ) {
        return ResponseEntity.ok()
                .build();
    }

    @Override
    @PostMapping("/meetings")
    public ResponseEntity<MeetingSaveResponse> save(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String fcmToken,
            @RequestBody MeetingSaveRequest meetingSaveRequest
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MeetingSaveResponse(1L, "우테코 16조", LocalDate.parse("2024-07-15"), LocalTime.parse("14:00"),
                        "서울 송파구 올림픽로35다길 42", "37.515298", "127.103113", 1, List.of(new MateResponse("오디")), "초대코드"));
    }
}
