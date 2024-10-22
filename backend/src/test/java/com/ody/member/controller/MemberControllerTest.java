package com.ody.member.controller;

import com.ody.auth.service.KakaoAuthUnlinkClient;
import com.ody.auth.token.AccessToken;
import com.ody.common.BaseControllerTest;
import com.ody.common.FixtureGenerator;
import com.ody.common.TokenFixture;
import com.ody.mate.domain.Mate;
import com.ody.meeting.domain.Meeting;
import com.ody.member.domain.Member;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;

@Import({FixtureGenerator.class})
class MemberControllerTest extends BaseControllerTest {

    @MockBean
    protected KakaoAuthUnlinkClient kakaoAuthUnlinkClient;

    @DisplayName("이미 삭제한 회원에 대한 회원 삭제 요청 시 204를 반환한다.")
    @Test
    void delete() {
        Member member = fixtureGenerator.generateMember();
        Meeting meeting = fixtureGenerator.generateMeeting();
        Mate mate = fixtureGenerator.generateMate(meeting, member);
        fixtureGenerator.generateEta(mate);
        fixtureGenerator.generateNotification(mate);

        AccessToken accessToken = TokenFixture.getValidAccessToken(member.getId());
        String authorization = "Bearer access-token=" + accessToken.getValue();

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header(HttpHeaders.AUTHORIZATION, authorization)
                .when().log().all()
                .delete("/members")
                .then()
                .statusCode(204);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header(HttpHeaders.AUTHORIZATION, authorization)
                .when().log().all()
                .delete("/members")
                .then()
                .statusCode(401);
    }
}
