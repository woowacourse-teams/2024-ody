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

    private final MeetingRepository meetingRepository;

    @Transactional
    public Meeting save(Meeting meeting) {
        Meeting savedMeeting = meetingRepository.save(meeting);
        String encodedInviteCode = InviteCodeGenerator.encode(meeting.getId());
        meeting.updateInviteCode(encodedInviteCode);
        return savedMeeting;
    }

    public Meeting findById(Long meetingId) {
        return meetingRepository.findById(meetingId)
                .orElseThrow(() -> new OdyNotFoundException("존재하지 않는 모임입니다."));
    }

    public List<Meeting> findAllMeetingsByMember(Member member) {
        return meetingRepository.findAllMeetingsByMember(member);
    }

    public void validateInvitedCode(String inviteCode) {
        Long meetingId = InviteCodeGenerator.decode(inviteCode);
        meetingRepository.findById(meetingId)
                .orElseThrow(() -> new OdyNotFoundException("존재하지 않는 초대 코드 입니다."));
    }
}
