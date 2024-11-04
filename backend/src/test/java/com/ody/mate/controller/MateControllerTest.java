package com.ody.mate.controller;

import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import com.ody.common.BaseControllerTest;
import com.ody.mate.dto.request.MateSaveRequestV2;
import com.ody.meeting.domain.Meeting;
import com.ody.member.domain.Member;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.LocalDateTime;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.http.HttpHeaders;

class MateControllerTest extends BaseControllerTest {


    @DisplayName("동일 약속에 퇴장했다가 재참여가 가능하다")
    @TestFactory
    Stream<DynamicTest> canReattendMeeting() {
        LocalDateTime fiveMinutesLater = LocalDateTime.now().plusMinutes(5L);
        Meeting meeting = fixtureGenerator.generateMeeting(fiveMinutesLater);
        Member member = fixtureGenerator.generateMember();

        return Stream.of(
                dynamicTest("약속에 최초 참여한다", () -> {
                    MateSaveRequestV2 mateSaveRequestV2 = dtoGenerator.generateMateSaveRequest(meeting);
                    RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .header(HttpHeaders.AUTHORIZATION,
                                    fixtureGenerator.generateAccessTokenValueByMember(member))
                            .body(mateSaveRequestV2)
                            .when()
                            .post("/v2/mates")
                            .then()
                            .statusCode(201);
                }),
                dynamicTest("약속에 최초 참여한다", () -> {
                    MateSaveRequestV2 mateSaveRequestV2 = dtoGenerator.generateMateSaveRequest(meeting);
                    RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .header(HttpHeaders.AUTHORIZATION, fixtureGenerator.generateAccessTokenValueByMember(member))
                            .body(mateSaveRequestV2)
                            .when()
                            .post("/v2/mates")
                            .then()
                            .statusCode(201);
                }),
                dynamicTest("약속에서 퇴장한다", () -> {
                    RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .header(HttpHeaders.AUTHORIZATION, fixtureGenerator.generateAccessTokenValueByMember(member))
                            .when()
                            .delete("/meetings/" + meeting.getId() + "/mate")
                            .then()
                            .statusCode(204);
                }),
                dynamicTest("약속에 재참여한다", () -> {
                    MateSaveRequestV2 mateSaveRequestV2 = dtoGenerator.generateMateSaveRequest(meeting);
                    RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .header(HttpHeaders.AUTHORIZATION, fixtureGenerator.generateAccessTokenValueByMember(member))
                            .body(mateSaveRequestV2)
                            .when()
                            .post("/v2/mates")
                            .then()
                            .statusCode(201);
                })
        );
    }

}
