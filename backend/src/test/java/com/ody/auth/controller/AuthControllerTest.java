package com.ody.auth.controller;

import com.ody.auth.dto.request.AuthRequest;
import com.ody.auth.token.AccessToken;
import com.ody.auth.token.RefreshToken;
import com.ody.common.BaseControllerTest;
import com.ody.common.TokenFixture;
import com.ody.member.domain.AuthProvider;
import com.ody.member.domain.DeviceToken;
import com.ody.member.domain.Member;
import com.ody.member.repository.MemberRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

class AuthControllerTest extends BaseControllerTest {

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("카카오 로그인 API")
    @Nested
    class authKakao {

        @DisplayName("비회원이 로그인하면 200을 반환한다.")
        @Test
        void authKakaoWithNonMember() {
            AuthRequest authRequest = generateAuthRequest("newPid", "newDeviceToken");

            RestAssured.given()
                    .contentType(ContentType.JSON)
                    .body(authRequest)
                    .when()
                    .post("/v1/auth/kakao")
                    .then()
                    .statusCode(200);
        }

        @DisplayName("회원이 로그인하면 200을 반환한다.")
        @Test
        void authKakaoWithMember() {
            saveMember("pid", "deviceToken");

            AuthRequest authRequest = generateAuthRequest("pid", "deviceToken");

            RestAssured.given()
                    .contentType(ContentType.JSON)
                    .body(authRequest)
                    .when()
                    .post("/v1/auth/kakao")
                    .then()
                    .statusCode(200);
        }

        @DisplayName("유효하지 않은 요청 바디로 로그인하면 400을 반환한다.")
        @ParameterizedTest
        @MethodSource("getWrongAuthRequestArguments")
        void authKakaoWithInvalidRequestBody(AuthRequest authRequest) {
            RestAssured.given()
                    .contentType(ContentType.JSON)
                    .body(authRequest)
                    .when()
                    .post("/v1/auth/kakao")
                    .then()
                    .statusCode(400);
        }

        private static Stream<AuthRequest> getWrongAuthRequestArguments() {
            return Stream.of(
                    new AuthRequest(null, "pId", "nickname", "imageUrl"),
                    new AuthRequest("deviceToken", null, "nickname", "imageUrl"),
                    new AuthRequest("deviceToken", "pId", null, "imageUrl"),
                    new AuthRequest("deviceToken", "pId", "nickname", null)
            );
        }

        private Member saveMember(String providerId, String deviceToken) {
            Member member = new Member(
                    1L,
                    new AuthProvider(providerId),
                    "nickname",
                    "imageUrl",
                    new DeviceToken(deviceToken),
                    new RefreshToken("refresh-token=refreshToken")
            );
            return memberRepository.save(member);
        }

        private AuthRequest generateAuthRequest(String providerId, String deviceToken) {
            return new AuthRequest(deviceToken, providerId, "nickname", "imageUrl");
        }
    }

    @DisplayName("액세스 토큰 갱신 API")
    @Nested
    class refreshAccessToken {

        private RefreshToken validRefreshToken;
        private RefreshToken expiredRefreshToken;
        private RefreshToken invalidRefreshToken;
        private Member memberWithValidRefreshToken;
        private Member memberWithExpiredRefreshToken;
        private Member memberWithInvalidRefreshToken;

        @BeforeEach
        void setUp() {
            validRefreshToken = TokenFixture.getValidRefreshToken();
            expiredRefreshToken = TokenFixture.getExpiredRefreshToken();
            invalidRefreshToken = TokenFixture.getInvalidRefreshToken();

            memberWithValidRefreshToken = saveMember(validRefreshToken);
            memberWithExpiredRefreshToken = saveMember(expiredRefreshToken);
            memberWithInvalidRefreshToken = saveMember(validRefreshToken);
        }

        @DisplayName("만료된 액세스 토큰, 유효한 리프레시 토큰으로 액세스 토큰 갱신하면 200을 반환한다.")
        @Test
        void refreshAccessTokenSuccess() {
            AccessToken accessToken = TokenFixture.getExpiredAccessToken(memberWithValidRefreshToken.getId());
            String authorization = generateAuthorization(accessToken, validRefreshToken);

            RestAssured.given()
                    .contentType(ContentType.JSON)
                    .header(HttpHeaders.AUTHORIZATION, authorization)
                    .when()
                    .post("/v1/auth/refresh")
                    .then()
                    .statusCode(200);
        }

        @DisplayName("만료되지 않은 액세스 토큰으로 액세스 토큰 갱신하면 200을 반환한다.")
        @Test
        void refreshAccessTokenWithUnexpiredAccessToken() {
            AccessToken accessToken = TokenFixture.getValidAccessToken(memberWithValidRefreshToken.getId());
            String authorization = generateAuthorization(accessToken, validRefreshToken);

            RestAssured.given()
                    .contentType(ContentType.JSON)
                    .header(HttpHeaders.AUTHORIZATION, authorization)
                    .when()
                    .post("/v1/auth/refresh")
                    .then()
                    .statusCode(200);
        }

        @DisplayName("만료된 액세스, 리프레시 토큰으로 액세스 토큰 갱신하면 401을 반환한다.")
        @Test
        void refreshAccessTokenWithExpiredRefreshToken() {
            AccessToken accessToken = TokenFixture.getExpiredAccessToken(memberWithExpiredRefreshToken.getId());
            String authorization = generateAuthorization(accessToken, expiredRefreshToken);

            RestAssured.given()
                    .contentType(ContentType.JSON)
                    .header(HttpHeaders.AUTHORIZATION, authorization)
                    .when()
                    .post("/v1/auth/refresh")
                    .then()
                    .statusCode(401);
        }

        @DisplayName("유효하지 않은 액세스 토큰으로 액세스 토큰 갱신하면 400을 반환한다.")
        @Test
        void refreshAccessTokenWithInvalidAccessToken() {
            AccessToken accessToken = TokenFixture.getInvalidAccessToken(memberWithValidRefreshToken.getId());
            String authorization = generateAuthorization(accessToken, validRefreshToken);

            RestAssured.given()
                    .contentType(ContentType.JSON)
                    .header(HttpHeaders.AUTHORIZATION, authorization)
                    .when()
                    .post("/v1/auth/refresh")
                    .then()
                    .statusCode(400);
        }

        @DisplayName("유효하지 않은 리프레시 토큰으로 액세스 토큰 갱신하면 400을 반환한다.")
        @Test
        void refreshAccessTokenWithInvalidRefreshToken() {
            AccessToken accessToken = TokenFixture.getExpiredAccessToken(memberWithInvalidRefreshToken.getId());
            String authorization = generateAuthorization(accessToken, invalidRefreshToken);

            RestAssured.given()
                    .contentType(ContentType.JSON)
                    .header(HttpHeaders.AUTHORIZATION, authorization)
                    .when()
                    .post("/v1/auth/refresh")
                    .then()
                    .statusCode(400);
        }
    }

    private Member saveMember(RefreshToken refreshToken) {
        Member member = new Member(
                1L,
                new AuthProvider("pid"),
                "nickname",
                "imageUrl",
                new DeviceToken("deviceToken"),
                new RefreshToken("refresh-token=" + refreshToken.getValue())
        );
        return memberRepository.save(member);
    }

    private String generateAuthorization(AccessToken accessToken, RefreshToken refreshToken) {
        return "Bearer access-token=" + accessToken.getValue() + " refresh-token=" + refreshToken.getValue();
    }
}
