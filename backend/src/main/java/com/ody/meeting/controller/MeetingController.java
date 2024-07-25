package com.ody.meeting.controller;

import com.ody.common.annotation.AuthMember;
import com.ody.mate.domain.Mate;
import com.ody.mate.dto.request.MateSaveRequest;
import com.ody.mate.service.MateService;
import com.ody.meeting.domain.Meeting;
import com.ody.meeting.dto.request.MeetingSaveRequest;
import com.ody.meeting.dto.response.MeetingSaveResponse;
import com.ody.meeting.dto.response.MeetingSaveResponses;
import com.ody.meeting.service.MeetingService;
import com.ody.member.domain.Member;
import com.ody.notification.domain.Notification;
import com.ody.notification.dto.response.NotiLogFindResponses;
import com.ody.notification.service.NotificationService;
import com.ody.util.InviteCodeGenerator;
import jakarta.validation.Valid;
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

@Slf4j
@RestController
@RequiredArgsConstructor
public class MeetingController implements MeetingControllerSwagger {

    private final MeetingService meetingService;
    private final MateService mateService;
    private final NotificationService notificationService;

    @Override
    @GetMapping("/meetings/me")
    public ResponseEntity<MeetingSaveResponses> findMine(@AuthMember Member member) {

        List<Meeting> memberMeetings = meetingService.findAllMeetingsByMember(member);
        List<MeetingSaveResponse> saveResponses = memberMeetings.stream()
                .map(this::makeSaveResponse)
                .toList();
        return ResponseEntity.ok(new MeetingSaveResponses(saveResponses));
    }

    private MeetingSaveResponse makeSaveResponse(Meeting meeting) {
        return MeetingSaveResponse.of(meeting, mateService.findAllByMeetingId(meeting.getId()));
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
    @PostMapping("/meetings")
    public ResponseEntity<MeetingSaveResponse> save(
            @AuthMember Member member,
            @Valid @RequestBody MeetingSaveRequest meetingSaveRequest
    ) {
        Meeting meeting = meetingService.save(meetingSaveRequest);

        MateSaveRequest mateSaveRequest = new MateSaveRequest(
                meeting.getInviteCode(),
                meetingSaveRequest.nickname(),
                meetingSaveRequest.originAddress(),
                meetingSaveRequest.originLatitude(),
                meetingSaveRequest.originLongitude()
        );
        Mate mate = mateService.save(mateSaveRequest, meeting, member);
        notificationService.saveAndSendDepartureReminder(meeting, mate, member.getDeviceToken());

        List<Mate> mates = mateService.findAllByMeetingId(meeting.getId());
        MeetingSaveResponse meetingSaveResponse = MeetingSaveResponse.of(meeting, mates);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(meetingSaveResponse);
    }

    @Override
    @GetMapping("/invite-codes/{inviteCode}/validate")
    public ResponseEntity<Void> validateInviteCode(
            @AuthMember Member member,
            @PathVariable String inviteCode
    ) {
        InviteCodeGenerator.decode(inviteCode);
        return ResponseEntity.ok()
                .build();
    }
}
