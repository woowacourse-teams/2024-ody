package com.ody.meeting.controller;

import com.ody.common.annotation.AuthMember;
import com.ody.mate.dto.request.MateStatusRequest;
import com.ody.meeting.dto.request.MeetingSaveRequest;
import com.ody.mate.dto.response.MateStatusResponses;
import com.ody.meeting.dto.response.MeetingSaveResponse;
import com.ody.meeting.dto.response.MeetingSaveResponses;
import com.ody.meeting.service.MeetingService;
import com.ody.member.domain.Member;
import com.ody.notification.domain.Notification;
import com.ody.notification.dto.response.NotiLogFindResponses;
import com.ody.notification.service.NotificationService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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

    @Override
    @PatchMapping("/v1/meetings/{meetingId}/mate-statuses")
    public ResponseEntity<MateStatusResponses> findAllMateStatuses(
            @AuthMember Member member,
            @PathVariable Long meetingId,
            @RequestBody MateStatusRequest mateStatusRequest
    ) {
        MateStatusResponses mateStatuses = meetingService.findAllMateStatuses(meetingId, mateStatusRequest);
        return ResponseEntity.ok(mateStatuses);
    }
}
