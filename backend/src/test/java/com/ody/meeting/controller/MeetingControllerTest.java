package com.ody.meeting.controller;

import com.ody.common.BaseControllerTest;
import com.ody.common.Fixture;
import com.ody.mate.domain.Mate;
import com.ody.mate.domain.Nickname;
import com.ody.mate.repository.MateRepository;
import com.ody.meeting.domain.Location;
import com.ody.meeting.domain.Meeting;
import com.ody.meeting.repository.MeetingRepository;
import com.ody.member.domain.Member;
import com.ody.member.repository.MemberRepository;
import io.restassured.RestAssured;
import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

class MeetingControllerTest extends BaseControllerTest {

    @Autowired
    MeetingRepository meetingRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    MateRepository mateRepository;

    @Disabled
    @DisplayName("특정 멤버의 참여 모임 목록 조회에 성공하면 200응답 반환한다")
    @Test
    void findMine() {
        meetingRepository.save(Fixture.ODY_MEETING1);
        memberRepository.save(Fixture.MEMBER1);
        mateRepository.save(Fixture.MATE1);

        RestAssured.given()
                .log()
                .all()
                .header(HttpHeaders.AUTHORIZATION, "Bearer device-token=" + Fixture.MEMBER1_TOKEN)
                .when()
                .get("/meetings/me")
                .then()
                .log()
                .all()
                .statusCode(200);
    }

    @DisplayName("로그 목록 조회에 성공하면 200응답 반환한다")
    @Test
    void findAllMeetingLogs() {
        Location origin = Fixture.ORIGIN_LOCATION;
        Location target = Fixture.TARGET_LOCATION;
        Member member = memberRepository.save(Fixture.MEMBER1);
        Meeting meeting = new Meeting("모임1", LocalDate.now(), LocalTime.now(), target, "초대코드1");
        meetingRepository.save(meeting);
        Mate mate = mateRepository.save(new Mate(meeting, member, new Nickname("은별"), origin));

        RestAssured.given()
                .log()
                .all()
                .header(HttpHeaders.AUTHORIZATION,
                        "Bearer device-token=" + Fixture.MEMBER1.getDeviceToken().getDeviceToken())
                .when()
                .get("/meetings/1/noti-log")
                .then()
                .log()
                .all()
                .statusCode(200);
    }
}
