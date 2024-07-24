package com.ody.meeting.controller;

import com.ody.common.BaseControllerTest;
import com.ody.meeting.dto.request.MeetingSaveRequest;
import com.ody.member.domain.DeviceToken;
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
}
