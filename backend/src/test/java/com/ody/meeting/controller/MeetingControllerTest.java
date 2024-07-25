package com.ody.meeting.controller;

import com.ody.common.BaseControllerTest;
import com.ody.common.Fixture;
import com.ody.mate.dto.request.MateSaveRequest;
import com.ody.mate.service.MateService;
import com.ody.meeting.domain.Location;
import com.ody.meeting.domain.Meeting;
import com.ody.meeting.dto.request.MeetingSaveRequest;
import com.ody.meeting.service.MeetingService;
import com.ody.member.domain.DeviceToken;
import com.ody.member.domain.Member;
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
    private MeetingService meetingService;

    @Autowired
    private MateService mateService;

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

    @DisplayName("로그 목록 조회에 성공하면 200응답 반환한다")
    @Test
    void findAllMeetingLogs() {
        String deviceToken = "Bearer device-token=testToken";
        Member member = memberService.save(new DeviceToken(deviceToken));

        Location target = Fixture.TARGET_LOCATION;
        Location origin = Fixture.ORIGIN_LOCATION;
        MeetingSaveRequest meetingRequest = new MeetingSaveRequest(
                "우테코 16조",
                LocalDate.now().plusDays(1),
                LocalTime.now().plusHours(1),
                target.getAddress(),
                target.getLatitude(),
                target.getLongitude(),
                "오디",
                origin.getAddress(),
                origin.getLatitude(),
                origin.getLongitude()
        );
        Meeting meeting = meetingService.save(meetingRequest);

        MateSaveRequest mateSaveRequest = new MateSaveRequest(
                meeting.getInviteCode(),
                "카키",
                "서울 강남구 테헤란로 411",
                "37.505713",
                "127.050691"
        );
        mateService.save(mateSaveRequest, meeting, member);

        RestAssured.given()
                .log()
                .all()
                .header(HttpHeaders.AUTHORIZATION, deviceToken)
                .when()
                .get("/meetings/1/noti-log")
                .then()
                .log()
                .all()
                .statusCode(200);
    }
}
