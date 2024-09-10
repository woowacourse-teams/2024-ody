package com.ody.eta.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.ody.common.Fixture;
import com.ody.common.FixtureGenerator;
import com.ody.common.config.JpaAuditingConfig;
import com.ody.eta.domain.Eta;
import com.ody.mate.domain.Mate;
import com.ody.mate.domain.Nickname;
import com.ody.mate.repository.MateRepository;
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
class EtaRepositoryTest {

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private MateRepository mateRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private EtaRepository etaRepository;

    @Autowired
    private FixtureGenerator fixtureGenerator;

    @Autowired
    private EntityManager entityManager;

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

        List<Eta> sojuMeetingEtas = etaRepository.findAllByMeetingId(sojuMeeting.getId());
        List<Eta> odyMeetingEtas = etaRepository.findAllByMeetingId(odyMeeting.getId());

        assertAll(
                () -> assertThat(sojuMeetingEtas).hasSize(1),
                () -> assertThat(odyMeetingEtas).hasSize(2)
        );
    }

    @DisplayName("Eta를 삭제(soft delete)한다.")
    @Test
    void delete() {
        Eta eta = fixtureGenerator.generateEta();

        etaRepository.delete(eta);

        Eta actual = (Eta) entityManager.createNativeQuery("select * from Eta where id = ?", Eta.class)
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
