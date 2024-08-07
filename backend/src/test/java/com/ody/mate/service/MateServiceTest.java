package com.ody.mate.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ody.common.BaseServiceTest;
import com.ody.common.Fixture;
import com.ody.common.exception.OdyBadRequestException;
import com.ody.mate.domain.Mate;
import com.ody.mate.domain.Nickname;
import com.ody.mate.dto.request.MateSaveRequest;
import com.ody.mate.repository.MateRepository;
import com.ody.meeting.domain.Meeting;
import com.ody.meeting.repository.MeetingRepository;
import com.ody.member.domain.Member;
import com.ody.member.repository.MemberRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MateServiceTest extends BaseServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private MateRepository mateRepository;

    @Autowired
    private MateService mateService;

    @DisplayName("모임 내 닉네임이 중복되지 않으면 모임에 참여한다.")
    @Test
    void saveMate() {
        Member member1 = memberRepository.save(Fixture.MEMBER1);
        Member member2 = memberRepository.save(Fixture.MEMBER2);

        Meeting odyMeeting = meetingRepository.save(Fixture.ODY_MEETING);
        mateRepository.save(new Mate(odyMeeting, member1, new Nickname("콜리"), Fixture.ORIGIN_LOCATION, 10L));

        MateSaveRequest mateSaveRequest = new MateSaveRequest(
                odyMeeting.getInviteCode(),
                "카키",
                Fixture.ORIGIN_LOCATION.getAddress(),
                Fixture.ORIGIN_LOCATION.getLatitude(),
                Fixture.ORIGIN_LOCATION.getLongitude()
        );
        assertThatCode(() -> mateService.saveAndSendNotifications(mateSaveRequest, member2, odyMeeting))
                .doesNotThrowAnyException();
    }

    @DisplayName("모임 내 닉네임이 중복되면 예외가 발생한다.")
    @Test
    void saveMateWithDuplicateNickname() {
        Member member1 = memberRepository.save(Fixture.MEMBER1);
        Member member2 = memberRepository.save(Fixture.MEMBER2);

        Meeting odyMeeting = meetingRepository.save(Fixture.ODY_MEETING);
        mateRepository.save(new Mate(odyMeeting, member1, new Nickname("제리"), Fixture.ORIGIN_LOCATION, 10L));

        MateSaveRequest mateSaveRequest = new MateSaveRequest(
                odyMeeting.getInviteCode(),
                "제리",
                Fixture.ORIGIN_LOCATION.getAddress(),
                Fixture.ORIGIN_LOCATION.getLatitude(),
                Fixture.ORIGIN_LOCATION.getLongitude()
        );
        assertThatThrownBy(() -> mateService.saveAndSendNotifications(mateSaveRequest, member2, odyMeeting))
                .isInstanceOf(OdyBadRequestException.class);
    }

    @DisplayName("회원이 참여하고 있는 특정 약속의 참여자 리스트를 조회한다.")
    @Test
    void findAllByMemberAndMeetingIdSuccess() {
        Member member1 = memberRepository.save(Fixture.MEMBER1);
        Member member2 = memberRepository.save(Fixture.MEMBER2);

        Meeting meeting = meetingRepository.save(Fixture.ODY_MEETING);

        Mate mate1 = mateRepository.save(new Mate(meeting, member1, new Nickname("조조"), Fixture.ORIGIN_LOCATION, 10L));
        Mate mate2 = mateRepository.save(new Mate(meeting, member2, new Nickname("제리"), Fixture.ORIGIN_LOCATION, 10L));

        List<Mate> mates = mateService.findAllByMemberAndMeetingId(member1, meeting.getId());
        List<Long> mateIds = mates.stream()
                .map(Mate::getId)
                .toList();

        assertThat(mateIds).containsOnly(mate1.getId(), mate2.getId());
    }

    @DisplayName("약속에 참여하고 있는 회원이 아니면 예외가 발생한다.")
    @Test
    void findAllByMemberAndMeetingIdException() {
        Member member1 = memberRepository.save(Fixture.MEMBER1);
        Member member2 = memberRepository.save(Fixture.MEMBER2);

        Meeting meeting = meetingRepository.save(Fixture.ODY_MEETING);
        mateRepository.save(new Mate(meeting, member1, new Nickname("조조"), Fixture.ORIGIN_LOCATION, 10L));

        assertThatThrownBy(() -> mateService.findAllByMemberAndMeetingId(member2, meeting.getId()))
                .isInstanceOf(OdyBadRequestException.class);
    }
}
