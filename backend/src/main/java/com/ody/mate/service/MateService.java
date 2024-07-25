package com.ody.mate.service;

import com.ody.common.exception.OdyBadRequestException;
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
        if (mateRepository.existsByMeetingIdAndNicknameNickname(meeting.getId(), mateSaveRequest.nickname())) {
            throw new OdyBadRequestException("모임 내 같은 닉네임이 존재합니다.");
        }

        if (mateRepository.existsByMeetingIdAndMemberId(meeting.getId(), member.getId())) {
            throw new OdyBadRequestException("모임에 이미 참여한 회원입니다.");
        }
        return mateRepository.save(mateSaveRequest.toMate(meeting, member));
    }

    public List<Mate> findAllByMeetingId(Long meetingId) {
        return mateRepository.findAllByMeetingId(meetingId);
    }
}
