package com.ody.member.controller;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;

@Import({FixtureGenerator.class})
class MemberControllerTest extends BaseControllerTest {

    @Autowired
    private FixtureGenerator fixtureGenerator;

    @DisplayName("회원 삭제 API 멱등성 검증")
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
                .statusCode(204);
    }
}
