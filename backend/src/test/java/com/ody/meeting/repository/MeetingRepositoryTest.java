package com.ody.meeting.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.ody.common.Fixture;
import com.ody.common.config.JpaAuditingConfig;
import com.ody.mate.domain.Mate;
import com.ody.mate.repository.MateRepository;
import com.ody.meeting.domain.Meeting;
import com.ody.member.domain.Member;
import com.ody.member.repository.MemberRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@Import(JpaAuditingConfig.class)
@DataJpaTest
class MeetingRepositoryTest {

    @Autowired
    MeetingRepository meetingRepository;

    @Autowired
    MateRepository mateRepository;

    @Autowired
    MemberRepository memberRepository;

    @DisplayName("특정 멤버의 모임 목록을 반환한다")
    @Test
    void findAllMeetingsByMember() {
        //MEMBER1 참여 모임 : ODY_MEETING1, ODY_MEETING2
        //MEMBER2 참여 모임 : ODY_MEETING1
        Meeting meeting1 = meetingRepository.save(Fixture.ODY_MEETING1);
        Meeting meeting2 = meetingRepository.save(Fixture.ODY_MEETING2);

        Member member1 = memberRepository.save(Fixture.MEMBER1);
        Member member2 = memberRepository.save(Fixture.MEMBER2);
        Member member3 = memberRepository.save(Fixture.MEMBER3);

        Mate mate1 = mateRepository.save(Fixture.MATE1);
        Mate mate2 = mateRepository.save(Fixture.MATE2);
        Mate mate3 = mateRepository.save(Fixture.MATE3);

        List<Meeting> memberOneMeetings = meetingRepository.findAllMeetingsByMember(member1);
        List<Meeting> memberTwoMeetings = meetingRepository.findAllMeetingsByMember(member2);

        assertAll(
                () -> assertThat(memberOneMeetings.size()).isEqualTo(2),
                () -> assertThat(memberTwoMeetings.size()).isEqualTo(1)
        );
    }
}
