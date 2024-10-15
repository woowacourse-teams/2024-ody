package com.ody.mate.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.ody.common.BaseRepositoryTest;
import com.ody.common.Fixture;
import com.ody.mate.domain.Mate;
import com.ody.mate.domain.Nickname;
import com.ody.meeting.domain.Meeting;
import com.ody.member.domain.Member;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MateRepositoryTest extends BaseRepositoryTest {

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
    void deleteById() {
        Mate mate = fixtureGenerator.generateMate();

        mateRepository.deleteById(mate.getId());

        entityManager.flush();
        Optional<Mate> actual = mateRepository.findById(mate.getId());
        assertThat(actual).isPresent();
        assertThat(actual.get().getDeletedAt()).isNotNull();
    }

    @DisplayName("멤버가 참여하고 있는 모든 mate를 찾는다.")
    @Test
    void findFetchedAllByMemberId() {
        Member jojo = fixtureGenerator.generateMember("jojo");
        Member jerry = fixtureGenerator.generateMember("jerry");

        Mate mate1 = fixtureGenerator.generateMate(jojo);
        Mate mate2 = fixtureGenerator.generateMate(jojo);
        Mate mate3 = fixtureGenerator.generateMate(jerry);

        List<Mate> matesByJojo = mateRepository.findFetchedAllByMemberId(jojo.getId());

        assertThat(matesByJojo).containsExactly(mate1, mate2);
    }
}
