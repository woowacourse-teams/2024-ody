package com.ody.member.controller;

import com.ody.common.BaseControllerTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

class MemberControllerTest extends BaseControllerTest {

    @DisplayName("Authorization 헤더로 device token을 받아 회원을 저장하면 201을 응답한다")
    @Test
    void save() {
        String authorization = "Bearer device-token=testToken";

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header(HttpHeaders.AUTHORIZATION, authorization)
                .when()
                .post("/members")
                .then()
                .statusCode(HttpStatus.CREATED.value());
    }
}
