package com.ody.mate.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.ody.common.Fixture;
import com.ody.mate.domain.Mate;
import com.ody.mate.domain.Nickname;
import com.ody.meeting.domain.Meeting;
import com.ody.meeting.repository.MeetingRepository;
import com.ody.member.domain.Member;
import com.ody.member.repository.MemberRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class MateRepositoryTest {

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MateRepository mateRepository;

    @DisplayName("모임 ID로 모임 참여자를 찾는다")
    @Test
    void findAllByMeetingId() {
        Member member1 = memberRepository.save(Fixture.MEMBER1);
        Member member2 = memberRepository.save(Fixture.MEMBER2);
        Member member3 = memberRepository.save(Fixture.MEMBER3);

        Meeting odyMeeting = meetingRepository.save(Fixture.ODY_MEETING);
        Meeting sojuMeeting = meetingRepository.save(Fixture.SOJU_MEETING);

        Mate mate1 = mateRepository.save(new Mate(odyMeeting, member1, new Nickname("콜리"), Fixture.ORIGIN_LOCATION, 10L));
        Mate mate2 = mateRepository.save(new Mate(odyMeeting, member2, new Nickname("조조"), Fixture.ORIGIN_LOCATION, 10L));
        mateRepository.save(new Mate(sojuMeeting, member3, new Nickname("카키"), Fixture.ORIGIN_LOCATION, 10L));

        List<Mate> meeting1Mates = mateRepository.findAllByMeetingId(odyMeeting.getId());

        assertThat(meeting1Mates).containsExactlyElementsOf(List.of(mate1, mate2));
    }
}
