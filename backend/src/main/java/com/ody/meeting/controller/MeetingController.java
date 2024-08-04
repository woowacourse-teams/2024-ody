package com.ody.meeting.controller;

import com.ody.common.annotation.AuthMember;
import com.ody.meeting.dto.response.MeetingFindByMemberResponse;
import com.ody.meeting.dto.response.MeetingFindByMemberResponses;
import com.ody.mate.dto.request.MateEtaRequest;
import com.ody.meeting.dto.request.MeetingSaveRequest;
import com.ody.mate.dto.response.MateEtaResponses;
import com.ody.meeting.dto.response.MeetingSaveResponse;
import com.ody.meeting.dto.response.MeetingSaveResponses;
import com.ody.meeting.dto.response.MeetingWithMatesResponse;
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
    @GetMapping("/v1/meetings/{meetingId}")
    public ResponseEntity<MeetingWithMatesResponse> findMeetingWithMates(
            @AuthMember Member member,
            @PathVariable Long meetingId
    ) {
        MeetingWithMatesResponse meetingWithMatesResponse = meetingService.findMeetingWithMates(member, meetingId);
        return ResponseEntity.ok(meetingWithMatesResponse);
    }

    @Override
    @GetMapping("/meetings/me")
    public ResponseEntity<MeetingSaveResponses> findMine(@AuthMember Member member) {
        MeetingSaveResponses meetingSaveResponses = meetingService.findAllMeetingsByMember(member);
        return ResponseEntity.ok(meetingSaveResponses);
    }

    @Override
    @GetMapping("/v1/meetings/me")
    public ResponseEntity<MeetingFindByMemberResponses> findByMemberV1(@AuthMember Member member) {
        MeetingFindByMemberResponse meetingSaveResponse1 = new MeetingFindByMemberResponse(
                1L,
                "조조와 저녁 초밥",
                2,
                LocalDate.of(2024, 9, 10),
                LocalTime.of(13, 30),
                "서울 테헤란로 411",
                "사당로22나길 22",
                30
        );
        MeetingFindByMemberResponse meetingSaveResponse2 = new MeetingFindByMemberResponse(
                2L,
                "올리브와 저녁 마라탕",
                7,
                LocalDate.of(2024, 9, 11),
                LocalTime.of(13, 30),
                "서울 테헤란로 411",
                "사당로22나길 22",
                30
        );

        MeetingFindByMemberResponses meetingFindByMemberResponses = new MeetingFindByMemberResponses(
                List.of(meetingSaveResponse1, meetingSaveResponse2)
        );
        return ResponseEntity.ok(meetingFindByMemberResponses);
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
    @PatchMapping("/v1/meetings/{meetingId}/mates/etas")
    public ResponseEntity<MateEtaResponses> findAllMateEtas(
            @AuthMember Member member,
            @PathVariable Long meetingId,
            @RequestBody MateEtaRequest mateEtaRequest
    ) {
        MateEtaResponses mateStatuses = meetingService.findAllMateEtas(meetingId, mateEtaRequest);
        return ResponseEntity.ok(mateStatuses);
    }
}
