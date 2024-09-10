package com.ody.common;

import com.ody.auth.dto.request.AuthRequest;
import com.ody.auth.dto.response.AuthResponse;
import com.ody.mate.dto.response.MateSaveResponse;
import com.ody.meeting.dto.request.MeetingSaveRequestV1;
import com.ody.notification.config.FcmConfig;
import com.ody.notification.service.FcmPushSender;
import com.ody.notification.service.FcmSubscriber;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;

@Import({TestRouteConfig.class, TestAuthConfig.class})
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class BaseControllerTest {

    @MockBean
    private FcmConfig fcmConfig;

    @MockBean
    protected FcmSubscriber fcmSubscriber;

    @MockBean
    protected FcmPushSender fcmPushSender;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @BeforeEach
    void databaseCleanUp() {
        databaseCleaner.cleanUp();
    }

    @LocalServerPort
    private int port;

    @BeforeEach
    void setPort() {
        RestAssured.port = port;
    }

    // TODO : 여기 이상해요
    protected String saveMember() {
        AuthRequest authRequest = new AuthRequest("dt", "pid", "제리", "imageUrl");

        AuthResponse authResponse = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(authRequest)
                .when()
                .post("/v1/auth/kakao")
                .then()
                .extract()
                .as(AuthResponse.class);

        String format = String.format(
                "%s access-token=%s refresh-token=%s",
                authResponse.tokenType(),
                authResponse.accessToken(),
                authResponse.refreshToken()
        );
        return String.format(
                "%s access-token=%s",
                authResponse.tokenType(),
                authResponse.accessToken()
//                authResponse.refreshToken()
        );
    }

    protected MateSaveResponse saveMate(LocalDate date, LocalTime time) {
        String authorization = saveMember();

        MeetingSaveRequestV1 meetingRequest = new MeetingSaveRequestV1(
                "기본 약속",
                date,
                time,
                "서울 송파구 올림픽로35다길 42",
                "37.515298",
                "127.103113"
        );

        return RestAssured.given()
                .contentType(ContentType.JSON)
                .header(HttpHeaders.AUTHORIZATION, authorization)
                .body(meetingRequest)
                .when()
                .post("/v1/mate")
                .then()
                .extract()
                .as(MateSaveResponse.class);
    }
}
