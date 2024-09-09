package com.ody.meeting.repository;

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
import com.ody.util.TimeUtil;
import java.time.LocalDateTime;
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

    @DisplayName("약속 날짜가 오늘 이전인 약속방들의 overdue 상태를 true로 변경한다")
    @Test
    void updateAllByOverdueMeetings() {
        LocalDateTime oneHoursLater = LocalDateTime.now().plusHours(1L);
        Meeting oneHoursLaterMeeting = new Meeting(
                "우테코 등교",
                oneHoursLater.toLocalDate(),
                oneHoursLater.toLocalTime(),
                TARGET_LOCATION,
                "초대코드"
        );
        meetingRepository.save(oneHoursLaterMeeting);

        LocalDateTime oneDayBefore = LocalDateTime.now().minusDays(1L);
        Meeting oneDayBeforeMeeting = new Meeting(
                "기상시간",
                oneDayBefore.toLocalDate(),
                oneDayBefore.toLocalTime(),
                TARGET_LOCATION,
                "초대코드"
        );
        meetingRepository.save(oneDayBeforeMeeting);

        LocalDateTime twoDayBefore = LocalDateTime.now().minusDays(2L);
        Meeting twoDayBeforeMeeting = new Meeting(
                "클라이밍",
                twoDayBefore.toLocalDate(),
                twoDayBefore.toLocalTime(),
                TARGET_LOCATION,
                "초대코드"
        );
        meetingRepository.save(twoDayBeforeMeeting);

        meetingRepository.updateAllByNotOverdueMeetings();

        assertThat(meetingRepository.findAll()).extracting(Meeting::isOverdue).containsExactly(false, true, true);
    }

    @DisplayName("오늘 약속의 기한이 지난 약속 리스트들을 조회한다")
    @Test
    void findAllByUpdatedTodayAndOverdue() {
        LocalDateTime oneHoursLater = LocalDateTime.now().plusHours(1L);
        Meeting oneHoursLaterMeeting = new Meeting(
                "우테코 등교",
                oneHoursLater.toLocalDate(),
                oneHoursLater.toLocalTime(),
                TARGET_LOCATION,
                "초대코드"
        );
        meetingRepository.save(oneHoursLaterMeeting);

        LocalDateTime oneDayBefore = LocalDateTime.now().minusDays(1L);
        Meeting oneDayBeforeMeeting = new Meeting(
                "기상시간",
                oneDayBefore.toLocalDate(),
                oneDayBefore.toLocalTime(),
                TARGET_LOCATION,
                "초대코드"
        );
        meetingRepository.save(oneDayBeforeMeeting);

        LocalDateTime twoDayBefore = LocalDateTime.now().minusDays(2L);
        Meeting twoDayBeforeMeeting = new Meeting(
                "클라이밍",
                twoDayBefore.toLocalDate(),
                twoDayBefore.toLocalTime(),
                TARGET_LOCATION,
                "초대코드"
        );
        meetingRepository.save(twoDayBeforeMeeting);

        meetingRepository.updateAllByNotOverdueMeetings();

        assertThat(meetingRepository.findAllByUpdatedTodayAndOverdue()).extracting(Meeting::getId)
                .containsExactly(oneDayBeforeMeeting.getId(), twoDayBeforeMeeting.getId());
    }

    @DisplayName("약속 기한이 지나지 않은 모임방을 조회한다.")
    @Test
    void findByIdAndOverdueFalse() {
        LocalDateTime dateTime = TimeUtil.nowWithTrim();
        Meeting overdueMeeting = new Meeting(
                null,
                "우테코 등교",
                dateTime.toLocalDate(),
                dateTime.toLocalTime(),
                TARGET_LOCATION,
                "초대코드",
                true
        );
        Meeting savedOverdueMeeting = meetingRepository.save(overdueMeeting);

        Meeting notOverdueMeeting = new Meeting(
                null,
                "오디 회식",
                dateTime.toLocalDate(),
                dateTime.toLocalTime(),
                TARGET_LOCATION,
                "초대코드",
                false
        );
        Meeting savedNotOverdueMeeting = meetingRepository.save(notOverdueMeeting);

        assertThat(meetingRepository.findByIdAndOverdueFalse(savedOverdueMeeting.getId())).isEmpty();
        assertThat(meetingRepository.findByIdAndOverdueFalse(savedNotOverdueMeeting.getId())).isPresent();
    }
}
