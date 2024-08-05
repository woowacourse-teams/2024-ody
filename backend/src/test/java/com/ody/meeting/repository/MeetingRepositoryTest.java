package com.ody.meeting.repository;

import static com.ody.common.Fixture.MEMBER1;
import static com.ody.common.Fixture.MEMBER2;
import static com.ody.common.Fixture.ORIGIN_LOCATION;
import static com.ody.common.Fixture.TARGET_LOCATION;
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
import java.time.LocalDate;
import java.time.LocalTime;
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
        Meeting meeting1 = new Meeting(
                "우테코 16조",
                LocalDate.now().plusDays(1),
                LocalTime.parse("14:00"),
                TARGET_LOCATION,
                "초대코드"
        );

        Meeting meeting2 = new Meeting(
                "우테코 16조",
                LocalDate.now().plusDays(1),
                LocalTime.parse("14:00"),
                TARGET_LOCATION,
                "초대코드"
        );

        Meeting odyMeeting = meetingRepository.save(Fixture.ODY_MEETING);
        Meeting sojuMeeting = meetingRepository.save(Fixture.SOJU_MEETING);

        Member member1 = memberRepository.save(MEMBER1);
        Member member2 = memberRepository.save(MEMBER2);
        Member member3 = memberRepository.save(Fixture.MEMBER3);

        mateRepository.save(new Mate(odyMeeting, member1, new Nickname("제리"), ORIGIN_LOCATION, 10L));
        mateRepository.save(new Mate(odyMeeting, member2, new Nickname("카키"), ORIGIN_LOCATION, 10L));
        mateRepository.save(new Mate(sojuMeeting, member1, new Nickname("올리브"), ORIGIN_LOCATION, 10L));

        List<Meeting> memberOneMeetings = meetingRepository.findAllMeetingsByMemberId(member1.getId());
        List<Meeting> memberTwoMeetings = meetingRepository.findAllMeetingsByMemberId(member2.getId());

        assertAll(
                () -> assertThat(memberOneMeetings.size()).isEqualTo(2),
                () -> assertThat(memberTwoMeetings.size()).isEqualTo(1)
        );
    }
}
