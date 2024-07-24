package com.ody.mate.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.ody.common.Fixture;
import com.ody.mate.domain.Mate;
import com.ody.meeting.domain.Meeting;
import com.ody.meeting.repository.MeetingRepository;
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
        Meeting meeting1 = meetingRepository.save(Fixture.ODY_MEETING1);
        meetingRepository.save(Fixture.ODY_MEETING2);

        memberRepository.save(Fixture.MEMBER1);
        memberRepository.save(Fixture.MEMBER2);
        memberRepository.save(Fixture.MEMBER3);

        mateRepository.save(Fixture.MATE1);
        mateRepository.save(Fixture.MATE2);
        mateRepository.save(Fixture.MATE3);

        List<Mate> meeting1Mates = mateRepository.findAllByMeetingId(meeting1.getId());

        assertThat(meeting1Mates).containsExactlyElementsOf(List.of(Fixture.MATE1, Fixture.MATE2));
    }
}
