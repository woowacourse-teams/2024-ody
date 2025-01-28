package com.ody.mate.controller;

import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.ody.common.BaseControllerTest;
import com.ody.mate.dto.request.MateSaveRequestV2;
import com.ody.meeting.domain.Meeting;
import com.ody.member.domain.Member;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;

class MateControllerTest extends BaseControllerTest {

    @DisplayName("바로 전송해야 하는 입장/출발 알림의 경우, 트랜잭션이 완료 된 후 알림을 조회한다")
    @Test
    void sendUnSavedMessage() throws InterruptedException, FirebaseMessagingException {
        Meeting tenMinutesLaterMeeting = fixtureGenerator.generateMeeting(LocalDateTime.now().plusMinutes(10L));
        Member member = fixtureGenerator.generateMember();
        MateSaveRequestV2 mateSaveRequestV2 = dtoGenerator.generateMateSaveRequest(tenMinutesLaterMeeting);

        CountDownLatch countDownLatch = new CountDownLatch(2);
        Mockito.doAnswer(invocation -> {
            countDownLatch.countDown();
            return null;
        }).when(firebaseMessaging).send(any(Message.class));

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header(HttpHeaders.AUTHORIZATION, fixtureGenerator.generateAccessTokenValueByMember(member))
                .body(mateSaveRequestV2)
                .when()
                .post("/v2/mates")
                .then()
                .statusCode(201);

        countDownLatch.await(3L, TimeUnit.SECONDS);

        verify(firebaseMessaging, atLeast(2))
                .send(any(Message.class));
    }

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
                dynamicTest("약속에서 퇴장한다", () -> {
                    RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .header(HttpHeaders.AUTHORIZATION,
                                    fixtureGenerator.generateAccessTokenValueByMember(member))
                            .when()
                            .delete("/meetings/" + meeting.getId() + "/mate")
                            .then()
                            .statusCode(204);
                }),
                dynamicTest("약속에 재참여한다", () -> {
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
                })
        );
    }

    @DisplayName("약속에 참여한 상태로 재참여가 불가하다")
    @TestFactory
    Stream<DynamicTest> canNotReattendMeetingWithoutLeave() {
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
                dynamicTest("동일 약속에 재참여를 시도한다", () -> {
                    MateSaveRequestV2 mateSaveRequestV2 = dtoGenerator.generateMateSaveRequest(meeting);
                    RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .header(HttpHeaders.AUTHORIZATION,
                                    fixtureGenerator.generateAccessTokenValueByMember(member))
                            .body(mateSaveRequestV2)
                            .when()
                            .post("/v2/mates")
                            .then()
                            .statusCode(400);
                })
        );
    }
}
