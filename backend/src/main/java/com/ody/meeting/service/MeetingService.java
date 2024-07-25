package com.ody.meeting.service;

import com.ody.common.exception.OdyNotFoundException;
import com.ody.meeting.domain.Meeting;
import com.ody.meeting.dto.request.MeetingSaveRequest;
import com.ody.meeting.repository.MeetingRepository;
import com.ody.member.domain.Member;
import com.ody.util.InviteCodeGenerator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MeetingService {

    private static final String DEFAULT_INVITE_CODE = "초대코드";

    private final MeetingRepository meetingRepository;

    @Transactional
    public Meeting save(MeetingSaveRequest meetingSaveRequest) {
        Meeting meeting = meetingRepository.save(meetingSaveRequest.toMeeting(DEFAULT_INVITE_CODE));
        String encodedInviteCode = InviteCodeGenerator.encode(meeting.getId());
        meeting.updateInviteCode(encodedInviteCode);
        return meeting;
    }

    public Meeting findById(Long meetingId) {
        return meetingRepository.findById(meetingId)
                .orElseThrow(() -> new OdyNotFoundException("존재하지 않는 모임입니다."));
    }

    public List<Meeting> findAllMeetingsByMember(Member member) {
        return meetingRepository.findAllMeetingsByMember(member);
    }

    public void validateInvitedCode(String inviteCode) {
        InviteCodeGenerator.decode(inviteCode);
        meetingRepository.findByInviteCode(inviteCode)
                .orElseThrow(() -> new OdyNotFoundException("존재하지 않는 초대 코드 입니다."));
    }
}
