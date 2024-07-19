package com.ody.mate.controller;

import com.ody.mate.domain.Mate;
import com.ody.mate.dto.request.MateSaveRequest;
import com.ody.mate.service.MateService;
import com.ody.meeting.domain.Meeting;
import com.ody.meeting.dto.response.MeetingSaveResponse;
import com.ody.meeting.service.MeetingService;
import com.ody.member.domain.Member;
import com.ody.member.service.MemberService;
import com.ody.notification.service.NotificationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MateController implements MateControllerSwagger {

    private final MeetingService meetingService;
    private final MemberService memberService;
    private final MateService mateService;
    private final NotificationService notificationService;

    @Override
    @PostMapping("/mates")
    public ResponseEntity<MeetingSaveResponse> save(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String deviceToken,
            MateSaveRequest mateSaveRequest
    ) {
        // TODO: MateSaveRequest -> inviteCode -> meetingId 디코딩으로 추출해야함
        Meeting meeting = meetingService.findById(1L);
        Member member = memberService.findByDeviceToken(deviceToken);
        Mate mate = mateService.save(mateSaveRequest, meeting, member);
        List<Mate> mates = mateService.findAllByMeetingId(meeting.getId());

        notificationService.saveAndSendDepartureReminder(meeting, mate, deviceToken);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(MeetingSaveResponse.of(meeting, mates));
    }
}
