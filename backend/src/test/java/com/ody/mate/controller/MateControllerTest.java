package com.ody.mate.controller;

import com.ody.common.BaseControllerTest;
import com.ody.common.Fixture;
import com.ody.mate.dto.request.MateSaveRequest;
import com.ody.meeting.domain.Meeting;
import com.ody.meeting.service.MeetingService;
import com.ody.member.domain.DeviceToken;
import com.ody.member.service.MemberService;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

@Disabled
class MateControllerTest extends BaseControllerTest {

    @Autowired
    private MeetingService meetingService;

    @Autowired
    private MemberService memberService;

    @DisplayName("Authorization 헤더로 device token을 받아 참여자를 저장하면 201을 응답한다")
    @Test
    void save() {
        String deviceToken = "Bearer device-token=testToken";
        memberService.save(new DeviceToken(deviceToken));

        Meeting meeting = meetingService.save(Fixture.ODY_MEETING1);

        MateSaveRequest mateSaveRequest = new MateSaveRequest(
                meeting.getInviteCode(),
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
                .statusCode(HttpStatus.CREATED.value())
                .contentType(ContentType.JSON);
    }
}

