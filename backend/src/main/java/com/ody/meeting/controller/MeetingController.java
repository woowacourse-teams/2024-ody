package com.ody.meeting.controller;

import com.ody.common.annotation.AuthMember;
import com.ody.meeting.dto.request.MeetingSaveRequest;
import com.ody.meeting.dto.request.MeetingSaveRequestV1;
import com.ody.meeting.dto.response.MeetingSaveResponse;
import com.ody.meeting.dto.response.MeetingSaveResponses;
import com.ody.meeting.dto.response.MeetingSaveResponseV1;
import com.ody.meeting.service.MeetingService;
import com.ody.member.domain.Member;
import com.ody.notification.domain.Notification;
import com.ody.notification.dto.response.NotiLogFindResponses;
import com.ody.notification.service.NotificationService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MeetingController implements MeetingControllerSwagger {

    private final MeetingService meetingService;
    private final NotificationService notificationService;

    @Override
    @PostMapping("/meetings")
    public ResponseEntity<MeetingSaveResponse> save(
            @AuthMember Member member,
            @Valid @RequestBody MeetingSaveRequest meetingSaveRequest
    ) {
        MeetingSaveResponse meetingSaveResponse = meetingService.saveAndSendNotifications(meetingSaveRequest, member);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(meetingSaveResponse);
    }

    @Override
    @PostMapping("/v1/meetings")
    public ResponseEntity<MeetingSaveResponseV1> saveV1(
            @AuthMember Member member,
            @Valid @RequestBody MeetingSaveRequestV1 meetingSaveRequestV1
    ) {
        MeetingSaveResponseV1 meetingSaveResponseV1 = new MeetingSaveResponseV1(
                1L,
                "우테코 16조",
                LocalDate.parse("2024-07-15"),
                LocalTime.parse("14:00"),
                "서울 송파구 올림픽로 35다길 42",
                "37.515298",
                "127.103113",
                "초대코드"
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(meetingSaveResponseV1);
    }

    @Override
    @GetMapping("/meetings/me")
    public ResponseEntity<MeetingSaveResponses> findMine(@AuthMember Member member) {
        MeetingSaveResponses meetingSaveResponses = meetingService.findAllMeetingsByMember(member);
        return ResponseEntity.ok(meetingSaveResponses);
    }

    @Override
    @GetMapping("/meetings/{meetingId}/noti-log")
    public ResponseEntity<NotiLogFindResponses> findAllMeetingLogs(
            @AuthMember Member member,
            @PathVariable Long meetingId
    ) {
        List<Notification> notifications = notificationService.findAllMeetingLogs(meetingId);
        NotiLogFindResponses response = NotiLogFindResponses.from(notifications);
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/invite-codes/{inviteCode}/validate")
    public ResponseEntity<Void> validateInviteCode(
            @AuthMember Member member,
            @PathVariable String inviteCode
    ) {
        meetingService.validateInviteCode(inviteCode);
        return ResponseEntity.ok()
                .build();
    }
}
