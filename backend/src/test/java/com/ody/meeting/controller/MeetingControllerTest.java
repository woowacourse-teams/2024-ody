package com.ody.meeting.controller;

import com.ody.common.BaseControllerTest;
import com.ody.common.Fixture;
import com.ody.mate.domain.Mate;
import com.ody.mate.domain.Nickname;
import com.ody.mate.repository.MateRepository;
import com.ody.meeting.domain.Location;
import com.ody.meeting.domain.Meeting;
import com.ody.meeting.dto.request.MeetingSaveRequest;
import com.ody.meeting.dto.request.MeetingSaveV1Request;
import com.ody.meeting.repository.MeetingRepository;
import com.ody.member.domain.DeviceToken;
import com.ody.member.domain.Member;
import com.ody.member.repository.MemberRepository;
import com.ody.member.service.MemberService;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

class MeetingControllerTest extends BaseControllerTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private MateRepository mateRepository;

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("Authorization 헤더로 device token과 모임 개설 정보를 받아 저장하면 201을 응답한다")
    @Test
    void save() {
        String deviceToken = "Bearer device-token=testToken";
        memberService.save(new DeviceToken(deviceToken));

        MeetingSaveRequest meetingRequest = new MeetingSaveRequest(
                "우테코 16조",
                LocalDate.now().plusDays(1),
                LocalTime.now().plusHours(1),
                "서울 송파구 올림픽로35다길 42",
                "37.515298",
                "127.103113",
                "오디",
                "서울 강남구 테헤란로 411",
                "37.505713",
                "127.050691"
        );

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header(HttpHeaders.AUTHORIZATION, deviceToken)
                .body(meetingRequest)
                .when()
                .post("/meetings")
                .then()
                .statusCode(HttpStatus.CREATED.value());
    }

    @DisplayName("모임 개설 성공 시, 201을 응답한다")
    @Test
    void saveV1() {
        String deviceToken = "Bearer device-token=testToken";
        memberService.save(new DeviceToken(deviceToken));

        MeetingSaveV1Request meetingRequest = new MeetingSaveV1Request(
                "우테코 16조",
                LocalDate.now().plusDays(1),
                LocalTime.now().plusHours(1),
                "서울 송파구 올림픽로35다길 42",
                "37.515298",
                "127.103113"
        );

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header(HttpHeaders.AUTHORIZATION, deviceToken)
                .body(meetingRequest)
                .when()
                .post("/v1/meetings")
                .then()
                .statusCode(HttpStatus.CREATED.value());
    }

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

    @DisplayName("유효하지 않은 초대 코드일 경우 404 에러를 반환한다.")
    @Test
    void validateInviteCode() {
        String deviceToken = "Bearer device-token=testToken";
        memberService.save(new DeviceToken(deviceToken));

        RestAssured.given()
                .header(HttpHeaders.AUTHORIZATION, deviceToken)
                .when()
                .get("/invite-codes/testcode/validate")
                .then()
                .log()
                .all()
                .statusCode(404);
    }
}
