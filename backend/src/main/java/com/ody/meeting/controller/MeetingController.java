package com.ody.meeting.controller;

import com.ody.common.annotation.AuthMember;
import com.ody.eta.domain.EtaStatus;
import com.ody.eta.dto.request.MateEtaRequest;
import com.ody.eta.dto.response.MateEtaResponse;
import com.ody.eta.dto.response.MateEtaResponses;
import com.ody.mate.dto.response.MateResponse;
import com.ody.mate.service.MateService;
import com.ody.meeting.dto.request.MeetingSaveRequest;
import com.ody.meeting.dto.request.MeetingSaveRequestV1;
import com.ody.meeting.dto.response.MateEtaResponsesV2;
import com.ody.meeting.dto.response.MeetingFindByMemberResponses;
import com.ody.meeting.dto.response.MeetingSaveResponse;
import com.ody.meeting.dto.response.MeetingSaveResponseV1;
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
    private final MateService mateService;

    @Override
    @PostMapping("/meetings")
    public ResponseEntity<MeetingSaveResponse> save(
            @AuthMember Member member,
            @Valid @RequestBody MeetingSaveRequest meetingSaveRequest
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
    @GetMapping("/meetings/me")
    public ResponseEntity<MeetingSaveResponses> findMine(@AuthMember Member member) {
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
        return ResponseEntity.ok(new MeetingSaveResponses(List.of(meetingSaveResponse)));
    }

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
    public ResponseEntity<MeetingWithMatesResponse> findMeetingWithMates(
            @AuthMember Member member,
            @PathVariable Long meetingId
    ) {
        MeetingWithMatesResponse meetingWithMatesResponse = meetingService.findMeetingWithMates(member, meetingId);
        return ResponseEntity.ok(meetingWithMatesResponse);
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
            @Valid @RequestBody MateEtaRequest mateEtaRequest
    ) {
        MateEtaResponses mateEtaResponses = new MateEtaResponses(
                "카키공주",
                new MateEtaResponse("콜리", EtaStatus.LATE_WARNING, 83),
                new MateEtaResponse("올리브", EtaStatus.ARRIVAL_SOON, 10),
                new MateEtaResponse("해음", EtaStatus.ARRIVED, 0),
                new MateEtaResponse("카키공주", EtaStatus.MISSING, -1)
        );
        return ResponseEntity.ok(mateEtaResponses);
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
