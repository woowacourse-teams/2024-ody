package com.ody.meeting.service;

import com.ody.common.exception.OdyNotFoundException;
import com.ody.mate.domain.Mate;
import com.ody.mate.dto.request.MateSaveRequest;
import com.ody.mate.dto.response.MateSaveResponse;
import com.ody.mate.repository.MateRepository;
import com.ody.mate.service.MateService;
import com.ody.meeting.domain.Meeting;
import com.ody.meeting.dto.request.MeetingSaveRequestV1;
import com.ody.meeting.dto.response.MeetingFindByMemberResponse;
import com.ody.meeting.dto.response.MeetingFindByMemberResponses;
import com.ody.meeting.dto.response.MeetingSaveResponseV1;
import com.ody.meeting.dto.response.MeetingWithMatesResponse;
import com.ody.meeting.repository.MeetingRepository;
import com.ody.member.domain.Member;
import com.ody.util.InviteCodeGenerator;
import java.util.Comparator;
import java.util.List;
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
    private final MateRepository mateRepository;

    @Transactional
    public MeetingSaveResponseV1 saveV1(MeetingSaveRequestV1 meetingSaveRequestV1) {
        Meeting meeting = meetingRepository.save(meetingSaveRequestV1.toMeeting(DEFAULT_INVITE_CODE));
        String encodedInviteCode = InviteCodeGenerator.encode(meeting.getId());
        meeting.updateInviteCode(encodedInviteCode);
        return MeetingSaveResponseV1.from(meeting);
    }

    public void validateInviteCode(String inviteCode) {
        try {
            findByInviteCode(inviteCode);
        } catch (OdyNotFoundException exception) {
            throw new OdyNotFoundException("존재하지 않는 초대 코드 입니다.");
        }
    }

    public Meeting findByInviteCode(String inviteCode) {
        Long meetingId = InviteCodeGenerator.decode(inviteCode);
        return findById(meetingId);
    }

    public Meeting findById(Long meetingId) {
        return meetingRepository.findById(meetingId)
                .orElseThrow(() -> new OdyNotFoundException("존재하지 않는 모임입니다."));
    }

    public MeetingFindByMemberResponses findAllByMember(Member member) {
        return meetingRepository.findAllByMemberId(member.getId())
                .stream()
                .map(meeting -> makeMeetingFindByMemberResponse(member, meeting))
                .sorted(Comparator.comparing(MeetingFindByMemberResponse::date)
                        .thenComparing(MeetingFindByMemberResponse::time))
                .collect(Collectors.collectingAndThen(Collectors.toList(), MeetingFindByMemberResponses::new));
    }

    private MeetingFindByMemberResponse makeMeetingFindByMemberResponse(Member member, Meeting meeting) {
        int mateCount = mateRepository.countByMeetingId(meeting.getId());
        Mate mate = mateRepository.findByMeetingIdAndMemberId(meeting.getId(), member.getId())
                .orElseThrow(() -> new OdyNotFoundException("참여하고 있지 않는 약속방입니다"));
        return MeetingFindByMemberResponse.of(meeting, mateCount, mate);
    }

    public MeetingWithMatesResponse findMeetingWithMates(Member member, Long meetingId) {
        Meeting meeting = findById(meetingId);
        List<Mate> mates = mateService.findAllByMemberAndMeetingId(member, meetingId);
        return MeetingWithMatesResponse.of(meeting, mates);
    }

    public MateSaveResponse saveMateAndSendNotifications(MateSaveRequest mateSaveRequest, Member member) {
        Meeting meeting = findByInviteCode(mateSaveRequest.inviteCode());
        return mateService.saveAndSendNotifications(mateSaveRequest, member, meeting);
    }
}
