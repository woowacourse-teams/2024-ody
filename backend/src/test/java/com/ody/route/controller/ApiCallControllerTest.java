package com.ody.route.controller;

import com.ody.common.BaseControllerTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ApiCallControllerTest extends BaseControllerTest {

    @DisplayName("ODsay API 호출 횟수 조회에 성공하면 200 응답을 반환한다")
    @Test
    void countOdsayApiCall() {
        RestAssured.given().log().all()
                .when()
                .get("/admin/api-call/count/odsay")
                .then().log().all()
                .statusCode(200);
    }

    @DisplayName("Google Maps API 호출 횟수 조회에 성공하면 200 응답을 반환한다")
    @Test
    void countGoogleApiCall() {
        RestAssured.given().log().all()
                .when()
                .get("/admin/api-call/count/google")
                .then().log().all()
                .statusCode(200);
    }
}
