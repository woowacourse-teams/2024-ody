package com.ody.meeting.service;

import com.ody.common.exception.OdyException;
import com.ody.meeting.domain.Meeting;
import com.ody.meeting.dto.request.MeetingSaveRequest;
import com.ody.meeting.repository.MeetingRepository;
import com.ody.util.InviteCodeGenerator;
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
                .orElseThrow(() -> new OdyException("존재하지 않는 모임입니다."));
    }
}
