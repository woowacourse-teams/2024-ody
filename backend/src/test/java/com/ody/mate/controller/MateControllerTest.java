package com.ody.mate.controller;

import com.ody.common.BaseControllerTest;
import com.ody.mate.dto.request.MateSaveRequest;
import com.ody.meeting.dto.request.MeetingSaveRequestV1;
import com.ody.meeting.dto.response.MeetingSaveResponseV1;
import com.ody.meeting.service.MeetingService;
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

class MateControllerTest extends BaseControllerTest {

    @Autowired
    private MeetingService meetingService;

    @Autowired
    private MemberService memberService;

    @DisplayName("Authorization 헤더로 device token을 받아 참여자를 저장하면 201을 응답한다")
    @Test
    void saveV1() {
        String deviceToken = "Bearer device-token=testToken";
        memberService.save(new DeviceToken(deviceToken));

        MeetingSaveRequestV1 meetingSaveRequestV1 = new MeetingSaveRequestV1(
                "우테코 16조",
                LocalDate.parse("2024-07-15"),
                LocalTime.parse("14:00"),
                "서울 송파구 올림픽로35다길 42",
                "37.515298",
                "127.103113"
        );
        MeetingSaveResponseV1 meetingSaveResponseV1 = meetingService.saveV1(meetingSaveRequestV1);

        MateSaveRequest mateSaveRequest = new MateSaveRequest(
                meetingSaveResponseV1.inviteCode(),
                "카키",
                "서울 강남구 테헤란로 411",
                "37.505713",
                "127.050691"
        );

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header(HttpHeaders.AUTHORIZATION, deviceToken)
                .body(mateSaveRequest)
                .when()
                .post("/mates")
                .then()
                .statusCode(201)
                .contentType(ContentType.JSON);
    }
}

