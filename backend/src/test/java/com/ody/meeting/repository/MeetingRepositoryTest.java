package com.ody.meeting.repository;

import static com.ody.common.Fixture.MATE1;
import static com.ody.common.Fixture.MATE2;
import static com.ody.common.Fixture.MATE3;
import static com.ody.common.Fixture.MEMBER1;
import static com.ody.common.Fixture.MEMBER2;
import static com.ody.common.Fixture.MEMBER3;
import static com.ody.common.Fixture.ODY_MEETING1;
import static com.ody.common.Fixture.ODY_MEETING2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.ody.common.config.JpaAuditingConfig;
import com.ody.mate.repository.MateRepository;
import com.ody.meeting.domain.Meeting;
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
        meetingRepository.save(ODY_MEETING1);
        meetingRepository.save(ODY_MEETING2);

        memberRepository.save(MEMBER1);
        memberRepository.save(MEMBER2);
        memberRepository.save(MEMBER3);

        mateRepository.save(MATE1);
        mateRepository.save(MATE2);
        mateRepository.save(MATE3);

        List<Meeting> memberOneMeetings = meetingRepository.findAllMeetingsByMember(MEMBER1);
        List<Meeting> memberTwoMeetings = meetingRepository.findAllMeetingsByMember(MEMBER2);

        assertAll(
                () -> assertThat(memberOneMeetings.size()).isEqualTo(2),
                () -> assertThat(memberTwoMeetings.size()).isEqualTo(1)
        );
    }
}
