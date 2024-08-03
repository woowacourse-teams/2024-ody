package com.ody.mate.service;

import com.ody.common.exception.OdyBadRequestException;
import com.ody.mate.domain.Mate;
import com.ody.mate.dto.request.MateSaveRequest;
import com.ody.mate.repository.MateRepository;
import com.ody.meeting.domain.Meeting;
import com.ody.meeting.dto.response.MeetingSaveResponse;
import com.ody.meeting.service.MeetingService;
import com.ody.member.domain.Member;
import com.ody.notification.service.NotificationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MateService {

    private final MateRepository mateRepository;
    private final MeetingService meetingService;
    private final NotificationService notificationService;

    @Transactional
    public MeetingSaveResponse saveAndSendNotifications(MateSaveRequest mateSaveRequest, Member member) {
        Meeting meeting = meetingService.findByInviteCode(mateSaveRequest.inviteCode());
        if (mateRepository.existsByMeetingIdAndNickname_Value(meeting.getId(), mateSaveRequest.nickname())) {
            throw new OdyBadRequestException("모임 내 같은 닉네임이 존재합니다.");
        }
        if (mateRepository.existsByMeetingIdAndMemberId(meeting.getId(), member.getId())) {
            throw new OdyBadRequestException("모임에 이미 참여한 회원입니다.");
        }
        Mate mate = mateRepository.save(mateSaveRequest.toMate(meeting, member));
        notificationService.saveAndSendNotifications(meeting, mate, member.getDeviceToken());
        return findAllByMeetingId(meeting);
    }

    public MeetingSaveResponse findAllByMeetingId(Meeting meeting) {
        List<Mate> mates = mateRepository.findAllByMeetingId(meeting.getId());
        return MeetingSaveResponse.of(meeting, mates);
    }
}
