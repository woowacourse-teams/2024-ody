package com.ody.meeting.controller;

import com.ody.mate.domain.Mate;
import com.ody.mate.domain.repository.MateRepository;
import com.ody.meeting.domain.Location;
import com.ody.meeting.domain.Meeting;
import com.ody.meeting.repository.MeetingRepository;
import com.ody.member.domain.Member;
import com.ody.member.repository.MemberRepository;
import io.restassured.RestAssured;
import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class MeetingControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    MeetingRepository meetingRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    MateRepository mateRepository;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @DisplayName("로그 목록 조회에 성공하면 200응답 반환한다")
    @Test
    void findAllMeetingLogs() {
        Location origin = new Location("출발지", "출발 위도", "출발 경도");
        Location target = new Location("도착지", "도착 위도", "도착 경도");
        Meeting meeting = new Meeting("모임1", LocalDate.now(), LocalTime.now(), target, "초대코드1");
        meetingRepository.save(meeting);
        Member member = memberRepository.save(new Member("token1"));
        Mate mate = mateRepository.save(new Mate(meeting, member, "은별", origin));

        RestAssured.given()
                .log()
                .all()
                .header(HttpHeaders.AUTHORIZATION, "device-token=token1")
                .when()
                .get("/meetings/1/noti-log")
                .then()
                .log()
                .all()
                .statusCode(200);
    }
}
