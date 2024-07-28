package com.ody.mate.controller;

import com.ody.common.annotation.AuthMember;
import com.ody.mate.domain.Mate;
import com.ody.mate.dto.request.MateSaveRequest;
import com.ody.mate.service.MateService;
import com.ody.meeting.domain.Meeting;
import com.ody.meeting.dto.response.MeetingSaveResponse;
import com.ody.meeting.service.MeetingService;
import com.ody.member.domain.Member;
import com.ody.notification.service.NotificationService;
import com.ody.util.InviteCodeGenerator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MateController implements MateControllerSwagger {

    private final MeetingService meetingService;
    private final MateService mateService;
    private final NotificationService notificationService;

    @Override
    @PostMapping("/mates")
    public ResponseEntity<MeetingSaveResponse> save(
            @AuthMember Member member,
            @RequestBody MateSaveRequest mateSaveRequest
    ) {
        Meeting meeting = meetingService.findById(InviteCodeGenerator.decode(mateSaveRequest.inviteCode()));
        Mate mate = mateSaveRequest.toMate(meeting, member);
        mateService.save(mate);
        notificationService.saveEntryAndDepartureNotification(mate);
        List<Mate> mates = mateService.findAllByMeetingId(meeting.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(MeetingSaveResponse.of(meeting, mates));
    }
}
