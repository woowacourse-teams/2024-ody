package com.ody.meeting.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.ody.common.BaseServiceTest;
import com.ody.common.Fixture;
import com.ody.mate.domain.Mate;
import com.ody.mate.domain.Nickname;
import com.ody.mate.repository.MateRepository;
import com.ody.meeting.domain.Meeting;
import com.ody.meeting.dto.response.MeetingFindByMemberResponse;
import com.ody.meeting.repository.MeetingRepository;
import com.ody.member.domain.Member;
import com.ody.member.repository.MemberRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MeetingServiceTest extends BaseServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private MateRepository mateRepository;

    @Autowired
    private MeetingService meetingService;

    @DisplayName("내 약속 목록 조회 시 오름차순 정렬한다.")
    @Test
    void findAllByMember() {
        Member member = memberRepository.save(Fixture.MEMBER1);

        Meeting meetingDayAfterTomorrowAt14 = meetingRepository.save(Fixture.ODY_MEETING4);
        Meeting meetingTomorrowAt12 = meetingRepository.save(Fixture.ODY_MEETING3);
        Meeting meetingTomorrowAt14 = meetingRepository.save(Fixture.ODY_MEETING1);

        mateRepository.save(new Mate(meetingDayAfterTomorrowAt14, member, new Nickname("제리1"), Fixture.ORIGIN_LOCATION));
        mateRepository.save(new Mate(meetingTomorrowAt12, member, new Nickname("제리2"), Fixture.ORIGIN_LOCATION));
        mateRepository.save(new Mate(meetingTomorrowAt14, member, new Nickname("제리3"), Fixture.ORIGIN_LOCATION));

        List<MeetingFindByMemberResponse> meetings = meetingService.findAllByMember(member).meetings();

        List<Long> meetingIds = meetings.stream()
                .map(MeetingFindByMemberResponse::id)
                .toList();

        assertThat(meetingIds).containsExactly(
                meetingTomorrowAt12.getId(),
                meetingTomorrowAt14.getId(),
                meetingDayAfterTomorrowAt14.getId()
        );
    }
}
