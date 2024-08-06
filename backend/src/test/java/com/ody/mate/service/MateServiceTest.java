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
import com.ody.meeting.domain.Location;
import com.ody.meeting.domain.Meeting;
import com.ody.meeting.dto.request.MeetingSaveRequestV1;
import com.ody.meeting.dto.response.MeetingSaveResponseV1;
import com.ody.meeting.repository.MeetingRepository;
import com.ody.meeting.service.MeetingService;
import com.ody.member.domain.Member;
import com.ody.member.repository.MemberRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MateServiceTest extends BaseServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MeetingService meetingService;

    @Autowired
    private MateRepository mateRepository;

    @Autowired
    private MateService mateService;

    @Autowired
    private MeetingRepository meetingRepository;

    @DisplayName("모임 내 닉네임이 중복되지 않으면 모임에 참여한다.")
    @Test
    void saveMate() {
        Member member1 = memberRepository.save(Fixture.MEMBER1);
        Member member2 = memberRepository.save(Fixture.MEMBER2);

        MeetingSaveRequestV1 meetingRequest = new MeetingSaveRequestV1(
                "우테코 16조",
                LocalDate.now().plusDays(1),
                LocalTime.now().plusHours(1),
                "서울 송파구 올림픽로35다길 42",
                "37.515298",
                "127.103113"
        );
        MeetingSaveResponseV1 meetingSaveResponseV1 = meetingService.saveV1(meetingRequest);
        Meeting meeting = new Meeting(
                meetingSaveResponseV1.id(),
                meetingSaveResponseV1.name(),
                meetingSaveResponseV1.date(),
                meetingSaveResponseV1.time(),
                new Location(
                        meetingSaveResponseV1.targetAddress(),
                        meetingSaveResponseV1.targetLatitude(),
                        meetingSaveResponseV1.targetLongitude()
                ),
                meetingSaveResponseV1.inviteCode()
        );

        mateRepository.save(
                new Mate(
                        meetingService.findById(meetingSaveResponseV1.id()),
                        member1,
                        new Nickname("콜리"),
                        Fixture.ORIGIN_LOCATION
                )
        );

        MateSaveRequest mateSaveRequest = new MateSaveRequest(
                meetingSaveResponseV1.inviteCode(),
                "카키",
                Fixture.ORIGIN_LOCATION.getAddress(),
                Fixture.ORIGIN_LOCATION.getLatitude(),
                Fixture.ORIGIN_LOCATION.getLongitude()
        );
        assertThatCode(() -> mateService.saveAndSendNotifications(mateSaveRequest, member2, meeting))
                .doesNotThrowAnyException();
    }

    @DisplayName("모임 내 닉네임이 중복되면 예외가 발생한다.")
    @Test
    void saveMateWithDuplicateNickname() {
        Member member1 = memberRepository.save(Fixture.MEMBER1);
        Member member2 = memberRepository.save(Fixture.MEMBER2);

        MeetingSaveRequestV1 meetingRequest = new MeetingSaveRequestV1(
                "우테코 16조",
                LocalDate.now().plusDays(1),
                LocalTime.now().plusHours(1),
                "서울 송파구 올림픽로35다길 42",
                "37.515298",
                "127.103113"
        );
        MeetingSaveResponseV1 meetingSaveResponseV1 = meetingService.saveV1(meetingRequest);
        Meeting meeting = new Meeting(
                meetingSaveResponseV1.id(),
                meetingSaveResponseV1.name(),
                meetingSaveResponseV1.date(),
                meetingSaveResponseV1.time(),
                new Location(
                        meetingSaveResponseV1.targetAddress(),
                        meetingSaveResponseV1.targetLatitude(),
                        meetingSaveResponseV1.targetLongitude()
                ),
                meetingSaveResponseV1.inviteCode()
        );

        Mate mate = mateRepository.save(
                new Mate(
                        meetingService.findById(meetingSaveResponseV1.id()),
                        member1,
                        new Nickname("제리"),
                        Fixture.ORIGIN_LOCATION
                )
        );

        MateSaveRequest mateSaveRequest = new MateSaveRequest(
                meetingSaveResponseV1.inviteCode(),
                mate.getNicknameValue(),
                Fixture.ORIGIN_LOCATION.getAddress(),
                Fixture.ORIGIN_LOCATION.getLatitude(),
                Fixture.ORIGIN_LOCATION.getLongitude()
        );
        assertThatThrownBy(() -> mateService.saveAndSendNotifications(mateSaveRequest, member2, meeting))
                .isInstanceOf(OdyBadRequestException.class);
    }

    @DisplayName("회원이 참여하고 있는 특정 약속의 참여자 리스트를 조회한다.")
    @Test
    void findAllByMemberAndMeetingIdSuccess() {
        Member member1 = memberRepository.save(Fixture.MEMBER1);
        Member member2 = memberRepository.save(Fixture.MEMBER2);

        Meeting meeting = meetingRepository.save(Fixture.ODY_MEETING1);

        Mate mate1 = mateRepository.save(new Mate(meeting, member1, new Nickname("조조"), Fixture.ORIGIN_LOCATION));
        Mate mate2 = mateRepository.save(new Mate(meeting, member2, new Nickname("제리"), Fixture.ORIGIN_LOCATION));

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

        Meeting meeting = meetingRepository.save(Fixture.ODY_MEETING1);
        mateRepository.save(new Mate(meeting, member1, new Nickname("조조"), Fixture.ORIGIN_LOCATION));

        assertThatThrownBy(() -> mateService.findAllByMemberAndMeetingId(member2, meeting.getId()))
                .isInstanceOf(OdyBadRequestException.class);
    }
}
