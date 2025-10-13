package com.ody.scenario.member;

import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;

import com.ody.auth.dto.request.AppleAuthRequest;
import com.ody.auth.dto.request.KakaoAuthRequest;
import com.ody.auth.service.apple.AppleRevokeTokenClient;
import com.ody.auth.service.apple.AppleValidateTokenClient;
import com.ody.auth.service.kakao.KakaoAuthUnlinkClient;
import com.ody.auth.token.AccessToken;
import com.ody.common.BaseControllerTest;
import com.ody.common.TokenFixture;
import com.ody.member.domain.Member;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.persistence.EntityManager;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.TestFactory;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpHeaders;

public class MemberScenarioTest extends BaseControllerTest {

    @Autowired
    private EntityManager entityManager;

    @MockBean
    private AppleValidateTokenClient appleValidateTokenClient;

    @SpyBean
    protected KakaoAuthUnlinkClient kakaoAuthUnlinkClient;

    @SpyBean
    protected AppleRevokeTokenClient appleRevokeTokenClient;

    @BeforeEach
    void setUp() {
        Mockito.when(appleValidateTokenClient.obtainRefreshToken(anyString()))
                .thenReturn("test-refresh-token");
        doNothing().when(appleRevokeTokenClient).unlink(any());

        doNothing().when(kakaoAuthUnlinkClient).unlink(any());
    }

    @Nested
    class AppleScenarioTest {

        private Member member;

        private AppleAuthRequest appleAuthRequest;

        @DisplayName("에플 회원 - 가입 - 탈퇴 - 재가입 - 탈퇴 시나리오")
        @TestFactory
        Stream<DynamicTest> apple_ReAttendAndDeleteSameMember() {
            return Stream.of(
                    dynamicTest("애플 회원 가입", () -> {
                        appleAuthRequest = dtoGenerator.generateAppleAuthRequest();

                        RestAssured.given().log().all()
                                .contentType(ContentType.JSON)
                                .body(appleAuthRequest)
                                .when()
                                .post("/v1/auth/apple")
                                .then()
                                .statusCode(200);

                        member = getUndeletedMemberByDeviceToken(appleAuthRequest.getDeviceToken());
                    }),
                    dynamicTest("애플 회원 탈퇴", () -> {
                        AccessToken accessToken = TokenFixture.getValidAccessToken(member.getId());
                        String authorization = "Bearer access-token=" + accessToken.getValue();

                        RestAssured.given().log().all()
                                .contentType(ContentType.JSON)
                                .header(HttpHeaders.AUTHORIZATION, authorization)
                                .when().log().all()
                                .delete("/v2/members")
                                .then()
                                .statusCode(204);
                    }),
                    dynamicTest("애플 회원 재가입", () -> {
                        RestAssured.given().log().all()
                                .contentType(ContentType.JSON)
                                .body(appleAuthRequest)
                                .when()
                                .post("/v1/auth/apple")
                                .then()
                                .statusCode(200);

                        member = getUndeletedMemberByDeviceToken(appleAuthRequest.getDeviceToken());
                    }),
                    dynamicTest("애플 회원 탈퇴", () -> {
                        AccessToken accessToken = TokenFixture.getValidAccessToken(member.getId());
                        String authorization = "Bearer access-token=" + accessToken.getValue();

                        Mockito.doNothing().when(appleRevokeTokenClient).unlink(any());

                        RestAssured.given().log().all()
                                .contentType(ContentType.JSON)
                                .header(HttpHeaders.AUTHORIZATION, authorization)
                                .when().log().all()
                                .delete("/v2/members")
                                .then()
                                .statusCode(204);
                    })
            );
        }
    }

    @Nested
    class KakaoScenarioTest {

        private Member member;

        private KakaoAuthRequest kakaoAuthRequest;

        @DisplayName("카카오 회원 - 가입 - 탈퇴 - 재가입 - 탈퇴 시나리오")
        @TestFactory
        Stream<DynamicTest> kako_ReAttendAndDeleteSameMember() {
            return Stream.of(
                    dynamicTest("카카오 회원 가입", () -> {
                        kakaoAuthRequest = dtoGenerator.generateKakaoAuthRequest("provider-id", "device-token");

                        RestAssured.given().log().all()
                                .contentType(ContentType.JSON)
                                .body(kakaoAuthRequest)
                                .when()
                                .post("/v1/auth/kakao")
                                .then()
                                .statusCode(200);

                        member = getUndeletedMemberByDeviceToken(kakaoAuthRequest.getDeviceToken());
                    }),
                    dynamicTest("카카오 회원 탈퇴", () -> {
                        AccessToken accessToken = TokenFixture.getValidAccessToken(member.getId());
                        String authorization = "Bearer access-token=" + accessToken.getValue();

                        RestAssured.given().log().all()
                                .contentType(ContentType.JSON)
                                .header(HttpHeaders.AUTHORIZATION, authorization)
                                .when().log().all()
                                .delete("/v2/members")
                                .then()
                                .statusCode(204);
                    }),
                    dynamicTest("카카오 회원 재가입", () -> {
                        kakaoAuthRequest = dtoGenerator.generateKakaoAuthRequest(
                                kakaoAuthRequest.getProviderId(),
                                kakaoAuthRequest.getDeviceToken()
                        );

                        RestAssured.given().log().all()
                                .contentType(ContentType.JSON)
                                .body(kakaoAuthRequest)
                                .when()
                                .post("/v1/auth/kakao")
                                .then()
                                .statusCode(200);

                        member = getUndeletedMemberByDeviceToken(kakaoAuthRequest.getDeviceToken());
                    }),
                    dynamicTest("카카오 재 회원 탈퇴", () -> {
                        AccessToken accessToken = TokenFixture.getValidAccessToken(member.getId());
                        String authorization = "Bearer access-token=" + accessToken.getValue();

                        RestAssured.given().log().all()
                                .contentType(ContentType.JSON)
                                .header(HttpHeaders.AUTHORIZATION, authorization)
                                .when().log().all()
                                .delete("/v2/members")
                                .then()
                                .statusCode(204);
                    })
            );
        }
    }

    private Member getUndeletedMemberByDeviceToken(String deviceToken) {
        return (Member) entityManager.createNativeQuery(
                        "select * from member where device_token=? and deleted_at is null", Member.class)
                .setParameter(1, deviceToken)
                .getSingleResult();
    }
}
