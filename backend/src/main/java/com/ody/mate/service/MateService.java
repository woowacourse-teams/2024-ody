package com.ody.mate.service;

import com.ody.common.exception.OdyBadRequestException;
import com.ody.mate.domain.Mate;
import com.ody.mate.dto.request.MateSaveRequest;
import com.ody.mate.dto.response.MateSaveResponse;
import com.ody.mate.repository.MateRepository;
import com.ody.meeting.domain.Meeting;
import com.ody.meeting.dto.response.MeetingFindByMemberResponse;
import com.ody.member.domain.Member;
import com.ody.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MateService {

    private final MateRepository mateRepository;
    private final NotificationService notificationService;

    @Transactional
    public MateSaveResponse saveAndSendNotifications(MateSaveRequest mateSaveRequest, Member member, Meeting meeting) {
        if (mateRepository.existsByMeetingIdAndNickname_Value(meeting.getId(), mateSaveRequest.nickname())) {
            throw new OdyBadRequestException("모임 내 같은 닉네임이 존재합니다.");
        }
        if (mateRepository.existsByMeetingIdAndMemberId(meeting.getId(), member.getId())) {
            throw new OdyBadRequestException("모임에 이미 참여한 회원입니다.");
        }
        Mate mate = mateRepository.save(mateSaveRequest.toMate(meeting, member));
        notificationService.saveAndSendNotifications(meeting, mate, member.getDeviceToken());
        return MateSaveResponse.from(mate);
    }

    public MeetingFindByMemberResponse findByMeetingAndMember(Meeting meeting, Member member) {
        int mateCount = mateRepository.countByMeetingId(meeting.getId());
        Mate mate = mateRepository.findByMeetingIdAndMemberId(meeting.getId(), member.getId());
        return MeetingFindByMemberResponse.of(meeting, mateCount, mate);
    }
}
