package com.ody.mate.service;

import com.ody.mate.domain.Mate;
import com.ody.mate.dto.request.MateSaveRequest;
import com.ody.mate.repository.MateRepository;
import com.ody.meeting.domain.Meeting;
import com.ody.member.domain.Member;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MateService {

    private final MateRepository mateRepository;

    @Transactional
    public Mate save(MateSaveRequest mateSaveRequest, Meeting meeting, Member member) {
        return mateRepository.save(mateSaveRequest.toMate(meeting, member));
    }

    public List<Mate> findAllByMeetingId(Long meetingId) {
        return mateRepository.findAllByMeetingId(meetingId);
    }
}
