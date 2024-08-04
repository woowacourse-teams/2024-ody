package com.ody.meeting.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.ody.common.BaseServiceTest;
import com.ody.common.Fixture;
import com.ody.mate.repository.MateRepository;
import com.ody.meeting.dto.response.MateResponse;
import com.ody.meeting.dto.response.MeetingWithMatesResponse;
import com.ody.meeting.repository.MeetingRepository;
import com.ody.member.domain.Member;
import com.ody.member.repository.MemberRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MeetingServiceTest extends BaseServiceTest {

    @Autowired
    private MeetingService meetingService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private MateRepository mateRepository;

    @DisplayName("약속과 참여자들 정보를 조회한다.")
    @Test
    void findMeetingWithMatesSuccess() {
        Member member1 = memberRepository.save(Fixture.MEMBER1);
        memberRepository.save(Fixture.MEMBER2);

        Long meetingId = meetingRepository.save(Fixture.ODY_MEETING1).getId();

        mateRepository.save(Fixture.MATE1);
        mateRepository.save(Fixture.MATE2);

        MeetingWithMatesResponse meetingWithMatesResponses = meetingService.findMeetingWithMates(member1, meetingId);
        List<String> mateNicknames = meetingWithMatesResponses.mates().stream()
                .map(MateResponse::nickname)
                .toList();

        assertAll(
                () -> assertThat(meetingWithMatesResponses.id()).isEqualTo(meetingId),
                () -> assertThat(mateNicknames).containsOnly(
                        Fixture.MATE1.getNicknameValue(),
                        Fixture.MATE2.getNicknameValue()
                )
        );
    }
}
