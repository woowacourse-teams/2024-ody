package com.ody.meeting.controller;

import com.ody.common.annotation.AuthMember;
import com.ody.eta.dto.request.MateEtaRequest;
import com.ody.mate.service.MateService;
import com.ody.meeting.dto.request.MeetingSaveRequestV1;
import com.ody.meeting.dto.response.MateEtaResponsesV2;
import com.ody.meeting.dto.response.MeetingFindByMemberResponses;
import com.ody.meeting.dto.response.MeetingSaveResponseV1;
import com.ody.meeting.dto.response.MeetingWithMatesResponseV1;
import com.ody.meeting.dto.response.MeetingWithMatesResponseV2;
import com.ody.meeting.service.MeetingService;
import com.ody.member.domain.Member;
import com.ody.notification.dto.response.NotiLogFindResponses;
import com.ody.notification.service.NotificationService;
import jakarta.validation.Valid;
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
    private final MateService mateService;
    private final NotificationService notificationService;

    @Override
    @PostMapping("/v1/meetings")
    public ResponseEntity<MeetingSaveResponseV1> saveV1(
            @AuthMember Member member,
            @Valid @RequestBody MeetingSaveRequestV1 meetingSaveRequestV1
    ) {
        MeetingSaveResponseV1 meetingSaveResponseV1 = meetingService.saveV1(meetingSaveRequestV1);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(meetingSaveResponseV1);
    }

    @GetMapping("/v1/meetings/{meetingId}")
    public ResponseEntity<MeetingWithMatesResponseV1> findMeetingWithMatesV1(
            @AuthMember Member member,
            @PathVariable Long meetingId
    ) {
        MeetingWithMatesResponseV1 meetingWithMatesResponse = meetingService.findMeetingWithMatesV1(member, meetingId);
        return ResponseEntity.ok(meetingWithMatesResponse);
    }

    @GetMapping("/v2/meetings/{meetingId}")
    public ResponseEntity<MeetingWithMatesResponseV2> findMeetingWithMatesV2(
            @AuthMember Member member,
            @PathVariable Long meetingId
    ) {
        MeetingWithMatesResponseV1 meetingWithMatesResponse = meetingService.findMeetingWithMatesV1(member, meetingId);
        return ResponseEntity.ok(null); //TODO 정상 응답으로 대체
    }

    @Override
    @GetMapping("/v1/meetings/me")
    public ResponseEntity<MeetingFindByMemberResponses> findAllByMember(@AuthMember Member member) {
        MeetingFindByMemberResponses meetingFindByMemberResponses = meetingService.findAllByMember(member);
        return ResponseEntity.ok(meetingFindByMemberResponses);
    }

    @Override
    @GetMapping("/meetings/{meetingId}/noti-log")
    public ResponseEntity<NotiLogFindResponses> findAllMeetingLogs(
            @AuthMember Member member,
            @PathVariable Long meetingId
    ) {
        NotiLogFindResponses response = notificationService.findAllNotiLogs(meetingId);
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/invite-codes/{inviteCode}/validate")
    public ResponseEntity<Void> validateInviteCode(
            @AuthMember Member member,
            @PathVariable String inviteCode
    ) {
        meetingService.validateInviteCode(member, inviteCode);
        return ResponseEntity.ok()
                .build();
    }

    @PatchMapping("/v2/meetings/{meetingId}/mates/etas")
    public ResponseEntity<MateEtaResponsesV2> findAllMateEtasV2(
            @AuthMember Member member,
            @PathVariable Long meetingId,
            @Valid @RequestBody MateEtaRequest mateEtaRequest
    ) {
        MateEtaResponsesV2 mateEtaResponses = mateService.findAllMateEtas(mateEtaRequest, meetingId, member);
        return ResponseEntity.ok(mateEtaResponses);
    }
}
