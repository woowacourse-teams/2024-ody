package com.ody.meeting.service;

import com.ody.common.exception.OdyNotFoundException;
import com.ody.mate.domain.EtaStatus;
import com.ody.mate.dto.request.MateEtaRequest;
import com.ody.mate.dto.response.MateEtaResponse;
import com.ody.mate.dto.response.MateEtaResponses;
import com.ody.mate.dto.response.MateResponse;
import com.ody.meeting.domain.Meeting;
import com.ody.meeting.dto.request.MeetingSaveRequestV1;
import com.ody.meeting.dto.response.MeetingSaveResponseV1;
import com.ody.meeting.dto.response.MeetingWithMatesResponse;
import com.ody.meeting.repository.MeetingRepository;
import com.ody.member.domain.Member;
import com.ody.util.InviteCodeGenerator;
import java.time.LocalDate;
import java.time.LocalTime;
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

    public MeetingWithMatesResponse findMeetingWithMates(Member member, Long meetingId) {
        return new MeetingWithMatesResponse(
                1L,
                "우테코 16조",
                LocalDate.parse("2024-07-15"),
                LocalTime.parse("14:00"),
                "서울 송파구 올림픽로35다길 42",
                "37.515298",
                "127.103113",
                2,
                List.of(new MateResponse("오디"), new MateResponse("제리")),
                "초대코드"
        );
    }

    public MateEtaResponses findAllMateEtas(Long meetingId, MateEtaRequest mateEtaRequest) {
        List<MateEtaResponse> mateStatuses = List.of(
                new MateEtaResponse("콜리", EtaStatus.LATE_WARNING, 83L),
                new MateEtaResponse("올리브", EtaStatus.ARRIVAL_SOON, 10L),
                new MateEtaResponse("해음", EtaStatus.ARRIVED, 0L),
                new MateEtaResponse("카키공주", EtaStatus.MISSING, -1L)
        );

        return new MateEtaResponses(mateStatuses);
    }
}
