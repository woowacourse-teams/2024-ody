package com.ody.mate.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ody.common.BaseServiceTest;
import com.ody.common.Fixture;
import com.ody.common.exception.OdyBadRequestException;
import com.ody.mate.domain.Mate;
import com.ody.mate.domain.Nickname;
import com.ody.meeting.domain.Meeting;
import com.ody.meeting.repository.MeetingRepository;
import com.ody.member.domain.Member;
import com.ody.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MateServiceTest extends BaseServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private MateService mateService;

    @DisplayName("모임 내 닉네임이 중복되지 않으면 모임에 참여한다.")
    @Test
    void saveMate() {
        Member member1 = memberRepository.save(Fixture.MEMBER1);
        Meeting meeting = meetingRepository.save(Fixture.ODY_MEETING1);
        mateService.save(new Mate(meeting, member1, new Nickname("콜리"), Fixture.ORIGIN_LOCATION));

        Member member2 = memberRepository.save(Fixture.MEMBER2);
        Mate kaki = new Mate(meeting, member2, new Nickname("카키"), Fixture.ORIGIN_LOCATION);

        assertThatCode(() -> mateService.save(kaki))
                .doesNotThrowAnyException();
    }

    @DisplayName("모임 내 닉네임이 중복되면 예외가 발생한다.")
    @Test
    void saveMateWithDuplicateNickname() {
        Member member1 = memberRepository.save(Fixture.MEMBER1);
        Meeting meeting = meetingRepository.save(Fixture.ODY_MEETING1);
        Nickname nickname = new Nickname("콜리");
        mateService.save(new Mate(meeting, member1, nickname, Fixture.ORIGIN_LOCATION));

        Member member2 = memberRepository.save(Fixture.MEMBER2);
        Mate sameNicknameMate = new Mate(meeting, member2, nickname, Fixture.ORIGIN_LOCATION);

        assertThatThrownBy(() -> mateService.save(sameNicknameMate))
                .isInstanceOf(OdyBadRequestException.class);
    }
}
