package com.ody.meeting.service;

import com.ody.common.exception.OdyNotFoundException;
import com.ody.eta.dto.request.MateEtaRequest;
import com.ody.eta.dto.response.MateEtaResponses;
import com.ody.eta.service.EtaService;
import com.ody.mate.dto.request.MateSaveRequest;
import com.ody.mate.service.MateService;
import com.ody.meeting.domain.Meeting;
import com.ody.meeting.dto.request.MeetingSaveRequest;
import com.ody.meeting.dto.response.MeetingSaveResponse;
import com.ody.meeting.dto.response.MeetingSaveResponses;
import com.ody.meeting.repository.MeetingRepository;
import com.ody.member.domain.Member;
import com.ody.util.InviteCodeGenerator;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MeetingService {

    private static final String DEFAULT_INVITE_CODE = "초대코드";

    private final MateService mateService;
    private final MeetingRepository meetingRepository;
    private final EtaService etaService;

    @Transactional
    public MeetingSaveResponse saveAndSendNotifications(MeetingSaveRequest meetingSaveRequest, Member member) {
        Meeting meeting = save(meetingSaveRequest);
        MateSaveRequest mateSaveRequest = meetingSaveRequest.toMateSaveRequest(meeting.getInviteCode());
        return mateService.saveAndSendNotifications(mateSaveRequest, meeting, member);
    }

    public Meeting save(MeetingSaveRequest meetingSaveRequest) {
        Meeting meeting = meetingRepository.save(meetingSaveRequest.toMeeting(DEFAULT_INVITE_CODE));
        String encodedInviteCode = InviteCodeGenerator.encode(meeting.getId());
        meeting.updateInviteCode(encodedInviteCode);
        return meeting;
    }

    public MeetingSaveResponse findAndSendNotifications(MateSaveRequest mateSaveRequest, Member member) {
        Meeting meeting = findByInviteCode(mateSaveRequest.inviteCode());
        return mateService.saveAndSendNotifications(mateSaveRequest, meeting, member);
    }

    public void validateInviteCode(String inviteCode) {
        try {
            findByInviteCode(inviteCode);
        } catch (OdyNotFoundException exception) {
            throw new OdyNotFoundException("존재하지 않는 초대 코드 입니다.");
        }
    }

    private Meeting findByInviteCode(String inviteCode) {
        Long meetingId = InviteCodeGenerator.decode(inviteCode);
        return findById(meetingId);
    }

    private Meeting findById(Long meetingId) {
        return meetingRepository.findById(meetingId)
                .orElseThrow(() -> new OdyNotFoundException("존재하지 않는 모임입니다."));
    }

    public MeetingSaveResponses findAllMeetingsByMemberId(Member member) {
        return meetingRepository.findAllMeetingsByMemberId(member.getId()).stream()
                .map(mateService::findAllByMeetingId)
                .collect(Collectors.collectingAndThen(Collectors.toList(), MeetingSaveResponses::new));
    }

    public MateEtaResponses findAllMateEtas(MateEtaRequest mateEtaRequest, Long meetingId, Member member) {
        return etaService.findAllMateEtas(mateEtaRequest, meetingId, member);
    }
}
