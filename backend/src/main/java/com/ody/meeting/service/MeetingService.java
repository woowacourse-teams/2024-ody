package com.ody.meeting.service;

import com.ody.common.exception.OdyException;
import com.ody.meeting.domain.Meeting;
import com.ody.meeting.dto.request.MeetingSaveRequest;
import com.ody.meeting.repository.MeetingRepository;
import com.ody.member.domain.Member;
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
    public Meeting save(MeetingSaveRequest meetingSaveRequest) {
        return meetingRepository.save(meetingSaveRequest.toMeeting(generateInviteCode()));
    }

    private String generateInviteCode() {
        return "초대코드";
    }

    public Meeting findById(Long meetingId) {
        return meetingRepository.findById(meetingId)
                .orElseThrow(() -> new OdyException("존재하지 않는 모임입니다."));
    }

    public List<Meeting> findAllMeetingsByMember(Member member) {
        return meetingRepository.findAllMeetingsByMember(member);
    }
}
