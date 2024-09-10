package com.ody.mate.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.ody.common.Fixture;
import com.ody.common.FixtureGenerator;
import com.ody.common.config.JpaAuditingConfig;
import com.ody.mate.domain.Mate;
import com.ody.mate.domain.Nickname;
import com.ody.meeting.domain.Meeting;
import com.ody.meeting.repository.MeetingRepository;
import com.ody.member.domain.Member;
import com.ody.member.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@Import({JpaAuditingConfig.class, FixtureGenerator.class})
@DataJpaTest
class MateRepositoryTest {

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MateRepository mateRepository;

    @Autowired
    private FixtureGenerator fixtureGenerator;

    @Autowired
    private EntityManager entityManager;

    @DisplayName("모임 ID로 모임 참여자를 찾는다")
    @Test
    void findAllByMeetingId() {
        Member member1 = memberRepository.save(Fixture.MEMBER1);
        Member member2 = memberRepository.save(Fixture.MEMBER2);
        Member member3 = memberRepository.save(Fixture.MEMBER3);

        Meeting odyMeeting = meetingRepository.save(Fixture.ODY_MEETING);
        Meeting sojuMeeting = meetingRepository.save(Fixture.SOJU_MEETING);

        Mate mate1 = fixtureGenerator.generateMate(odyMeeting, member1);
        Mate mate2 = fixtureGenerator.generateMate(odyMeeting, member2);
        fixtureGenerator.generateMate(sojuMeeting, member3);

        List<Mate> meeting1Mates = mateRepository.findAllByOverdueFalseMeetingId(odyMeeting.getId());

        assertThat(meeting1Mates).containsExactlyElementsOf(List.of(mate1, mate2));
    }

    @DisplayName("약속 아이디로 약속 인원 수를 찾는다.")
    @Test
    void countByMeetingId() {
        Member member1 = memberRepository.save(Fixture.MEMBER1);
        Member member2 = memberRepository.save(Fixture.MEMBER2);
        Member member3 = memberRepository.save(Fixture.MEMBER3);
        Meeting meeting = meetingRepository.save(Fixture.ODY_MEETING);

        mateRepository.save(new Mate(meeting, member1, new Nickname("콜리"), Fixture.ORIGIN_LOCATION, 10L));
        mateRepository.save(new Mate(meeting, member2, new Nickname("조조"), Fixture.ORIGIN_LOCATION, 10L));
        mateRepository.save(new Mate(meeting, member3, new Nickname("카키"), Fixture.ORIGIN_LOCATION, 10L));

        int mateCount = mateRepository.countByMeetingId(meeting.getId());

        assertThat(mateCount).isEqualTo(3);
    }

    @DisplayName("약속 아이디와 멤버 아이디로 메이트를 찾는다.")
    @Test
    void findByMeetingIdAndMemberId() {
        Member member1 = memberRepository.save(Fixture.MEMBER1);
        Member member2 = memberRepository.save(Fixture.MEMBER2);
        Member member3 = memberRepository.save(Fixture.MEMBER3);
        Meeting meeting = meetingRepository.save(Fixture.ODY_MEETING);

        Mate expectedMate = mateRepository.save(
                new Mate(meeting, member1, new Nickname("콜리"), Fixture.ORIGIN_LOCATION, 10L)
        );
        mateRepository.save(new Mate(meeting, member2, new Nickname("조조"), Fixture.ORIGIN_LOCATION, 10L));
        mateRepository.save(new Mate(meeting, member3, new Nickname("카키"), Fixture.ORIGIN_LOCATION, 10L));

        Mate actualMate = mateRepository.findByMeetingIdAndMemberId(meeting.getId(), member1.getId()).get();

        assertThat(actualMate.getId()).isEqualTo(expectedMate.getId());
    }

    @DisplayName("참여자를 삭제(soft delete)한다.")
    @Test
    void delete() {
        Mate mate = fixtureGenerator.generateMate();

        mateRepository.delete(mate);

        Mate actual = (Mate) entityManager.createNativeQuery("select * from Mate where id = ?", Mate.class)
                .setParameter(1, mate.getId())
                .getSingleResult();
        assertThat(actual.getDeletedAt()).isNotNull();
    }

    @DisplayName("삭제된 참여자는 조회하지 않는다.")
    @Test
    void doNotFindDeletedMate() {
        Mate mate = fixtureGenerator.generateMate();

        mateRepository.delete(mate);

        Optional<Mate> actual = mateRepository.findById(mate.getId());
        assertThat(actual).isNotPresent();
    }
}
