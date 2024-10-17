package com.ody.eta.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.ody.common.BaseRepositoryTest;
import com.ody.common.Fixture;
import com.ody.eta.domain.Eta;
import com.ody.mate.domain.Mate;
import com.ody.mate.domain.Nickname;
import com.ody.meeting.domain.Meeting;
import com.ody.member.domain.Member;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EtaRepositoryTest extends BaseRepositoryTest {

    @DisplayName("특정 약속 참여자들의 Eta 목록을 반환한다")
    @Test
    void findAllByMeetingId() {
        Meeting odyMeeting = meetingRepository.save(Fixture.ODY_MEETING);
        Meeting sojuMeeting = meetingRepository.save(Fixture.SOJU_MEETING);

        Member member1 = memberRepository.save(Fixture.MEMBER1);
        Member member2 = memberRepository.save(Fixture.MEMBER2);
        Member member3 = memberRepository.save(Fixture.MEMBER3);

        Mate odyMate1 = mateRepository.save(
                new Mate(odyMeeting, member1, new Nickname("콜리"), Fixture.ORIGIN_LOCATION, 10L)
        );
        Mate odyMate2 = mateRepository.save(
                new Mate(odyMeeting, member2, new Nickname("카키"), Fixture.ORIGIN_LOCATION, 10L)
        );
        Mate sojuMate = mateRepository.save(
                new Mate(sojuMeeting, member3, new Nickname("제리"), Fixture.ORIGIN_LOCATION, 10L)
        );

        etaRepository.save(new Eta(odyMate1, 10L));
        etaRepository.save(new Eta(odyMate2, 15L));
        etaRepository.save(new Eta(sojuMate, 20L));

        List<Eta> odyMeetingEtas = etaRepository.findAllByMeetingId(odyMeeting.getId());
        List<Eta> sojuMeetingEtas = etaRepository.findAllByMeetingId(sojuMeeting.getId());

        assertAll(
                () -> assertThat(odyMeetingEtas).hasSize(2),
                () -> assertThat(sojuMeetingEtas).hasSize(1)
        );
    }

    @DisplayName("Eta를 삭제(soft delete)한다.")
    @Test
    void delete() {
        Eta eta = fixtureGenerator.generateEta();

        etaRepository.delete(eta);

        Eta actual = (Eta) entityManager.createNativeQuery("select * from eta where id = ?", Eta.class)
                .setParameter(1, eta.getId())
                .getSingleResult();
        assertThat(actual.getDeletedAt()).isNotNull();
    }

    @DisplayName("삭제된 Eta는 조회하지 않는다.")
    @Test
    void doNotFindDeletedEta() {
        Eta eta = fixtureGenerator.generateEta();

        etaRepository.delete(eta);

        Optional<Eta> actual = etaRepository.findById(eta.getId());
        assertThat(actual).isNotPresent();
    }
}
