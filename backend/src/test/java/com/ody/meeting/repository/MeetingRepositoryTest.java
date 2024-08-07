package com.ody.meeting.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.ody.common.Fixture;
import com.ody.common.config.JpaAuditingConfig;
import com.ody.mate.domain.Mate;
import com.ody.mate.domain.Nickname;
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


    @DisplayName("특정 멤버의 약속 목록을 반환한다.")
    @Test
    void findAllByMemberId() {
        Member member1 = memberRepository.save(Fixture.MEMBER1);
        Member member2 = memberRepository.save(Fixture.MEMBER2);
        Meeting odyMeeting = meetingRepository.save(Fixture.ODY_MEETING);
        Meeting sojuMeeting = meetingRepository.save(Fixture.SOJU_MEETING);

        mateRepository.save(new Mate(odyMeeting, member1, new Nickname("조조"), Fixture.ORIGIN_LOCATION, 10L));
        mateRepository.save(new Mate(sojuMeeting, member1, new Nickname("카키 같은 조조"), Fixture.ORIGIN_LOCATION, 10L));

        mateRepository.save(new Mate(odyMeeting, member2, new Nickname("콜리"), Fixture.ORIGIN_LOCATION, 10L));

        List<Meeting> member1Meetings = meetingRepository.findAllByMemberId(member1.getId());
        List<Meeting> member2Meetings = meetingRepository.findAllByMemberId(member2.getId());

        assertAll(
                () -> assertThat(member1Meetings).hasSize(2),
                () -> assertThat(member2Meetings).hasSize(1)
        );
    }
}
