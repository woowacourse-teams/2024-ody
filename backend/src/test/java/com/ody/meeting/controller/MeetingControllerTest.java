package com.ody.meeting.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.ody.common.BaseControllerTest;
import com.ody.common.DtoGenerator;
import com.ody.eta.domain.Eta;
import com.ody.eta.domain.EtaStatus;
import com.ody.eta.dto.request.MateEtaRequest;
import com.ody.mate.domain.Mate;
import com.ody.meeting.domain.Meeting;
import com.ody.meeting.dto.request.MeetingSaveRequestV1;
import com.ody.meeting.dto.response.MateEtaResponseV2;
import com.ody.meeting.dto.response.MateEtaResponsesV2;
import com.ody.meeting.dto.response.MeetingFindByMemberResponses;
import com.ody.meeting.dto.response.MeetingSaveResponseV1;
import com.ody.member.domain.Member;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.LocalDateTime;
import java.util.regex.Pattern;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

class MeetingControllerTest extends BaseControllerTest {

    private static String LOCALTIME_FORMAT = "HH:mm";

    private DtoGenerator dtoGenerator = new DtoGenerator();

    @DisplayName("모임 개설 성공 시, 201을 응답한다")
    @Test
    void saveV1() {
        Member member = fixtureGenerator.generateMember();

        MeetingSaveRequestV1 meetingRequest = dtoGenerator.generateMeetingRequest(LocalDateTime.now().plusDays(1L));

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header(HttpHeaders.AUTHORIZATION, fixtureGenerator.generateAccessTokenValueByMember(member))
                .body(meetingRequest)
                .when()
                .post("/v1/meetings")
                .then()
                .statusCode(201)
                .extract()
                .as(MeetingSaveResponseV1.class)
                .time().toString()
                .matches(LOCALTIME_FORMAT);
    }

    @DisplayName("특정 멤버의 참여 모임 목록 조회에 성공하면 200응답 반환한다")
    @Test
    void findMine() {
        Member member = fixtureGenerator.generateMember();
        Meeting odyMeeting = fixtureGenerator.generateMeeting();
        Mate mate = fixtureGenerator.generateMate(odyMeeting, member);

        RestAssured.given().log().all()
                .header(HttpHeaders.AUTHORIZATION, fixtureGenerator.generateAccessTokenValueByMember(member))
                .when()
                .get("/v1/meetings/me")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(MeetingFindByMemberResponses.class)
                .meetings()
                .stream()
                .map(response -> response.time().toString())
                .allMatch(time -> Pattern.matches(time, LOCALTIME_FORMAT));
    }

    @DisplayName("로그 목록 조회에 성공하면 200응답 반환한다")
    @Test
    void findAllMeetingLogs() {
        Member member = fixtureGenerator.generateMember();
        Meeting meeting = fixtureGenerator.generateMeeting();
        Mate mate = fixtureGenerator.generateMate(meeting, member);

        RestAssured.given().log().all()
                .header(HttpHeaders.AUTHORIZATION, fixtureGenerator.generateAccessTokenValueByMember(member))
                .when()
                .get("/meetings/1/noti-log")
                .then().log().all()
                .statusCode(200);
    }

    @DisplayName("유효하지 않은 초대 코드일 경우 404 에러를 반환한다.")
    @Test
    void validateInviteCode() {
        Member member = fixtureGenerator.generateMember();

        RestAssured.given()
                .header(HttpHeaders.AUTHORIZATION, fixtureGenerator.generateAccessTokenValueByMember(member))
                .when()
                .get("/invite-codes/testcode/validate")
                .then().log().all()
                .statusCode(404);
    }

    @DisplayName("Eta API 테스트")
    @Nested
    class EtaTest {

        @DisplayName("참여자들의 위치 현황 목록 성공하면 200응답 반환한다")
        @Test
        void findAllMateEtas() {
            Member member = fixtureGenerator.generateMember();
            Meeting meeting = fixtureGenerator.generateMeeting();
            Mate mate = fixtureGenerator.generateMate(meeting, member);
            Eta eta = fixtureGenerator.generateEta(mate);

            MateEtaRequest mateEtaRequest = dtoGenerator.generateMateRequest();

            RestAssured.given().log().all()
                    .header(HttpHeaders.AUTHORIZATION, fixtureGenerator.generateAccessTokenValueByMember(member))
                    .body(mateEtaRequest)
                    .contentType(ContentType.JSON)
                    .when()
                    .patch("/v2/meetings/1/mates/etas")
                    .then().log().all()
                    .statusCode(200);
        }

        @DisplayName("위치 권한을 껐을 때는 MISSING, 다시 켰을 때는 정상 상태와 소요시간을 반환한다")
        @Test
        void returnMissing_When_isMissing_returnEtaStatus_When_isNotMissing() {
            Member member = fixtureGenerator.generateMember();
            Meeting meeting = fixtureGenerator.generateMeeting();
            Mate mate = fixtureGenerator.generateMate(meeting, member);
            Eta eta = fixtureGenerator.generateEta(mate);

            MateEtaRequest mateEtaMissingRequest = dtoGenerator.generateMateRequest(true);

            MateEtaResponseV2 mateEtaMissingResponse = RestAssured.given().log().all()
                    .header(HttpHeaders.AUTHORIZATION, fixtureGenerator.generateAccessTokenValueByMember(member))
                    .body(mateEtaMissingRequest)
                    .contentType(ContentType.JSON)
                    .when()
                    .patch("/v2/meetings/1/mates/etas")
                    .then().log().all()
                    .statusCode(200)
                    .extract()
                    .as(MateEtaResponsesV2.class)
                    .mateEtas().get(0);

            assertThat(mateEtaMissingResponse.status()).isEqualTo(EtaStatus.MISSING);

            MateEtaRequest mateEtaNotMissingRequest = dtoGenerator.generateMateRequest(false);

            MateEtaResponseV2 mateEtaNotMissingResponse = RestAssured.given().log().all()
                    .header(HttpHeaders.AUTHORIZATION, fixtureGenerator.generateAccessTokenValueByMember(member))
                    .body(mateEtaNotMissingRequest)
                    .contentType(ContentType.JSON)
                    .when()
                    .patch("/v2/meetings/1/mates/etas")
                    .then().log().all()
                    .statusCode(200)
                    .extract()
                    .as(MateEtaResponsesV2.class)
                    .mateEtas().get(0);

            assertThat(mateEtaNotMissingResponse.status()).isNotEqualTo(EtaStatus.MISSING);
        }

        @DisplayName("MISSING 상태인 참여자만 MISSING 상태를 반환한다.")
        @Test
        void returnMissing_When_isMissing_Mate() {
            Meeting meeting = fixtureGenerator.generateMeeting();

            Member member1 = fixtureGenerator.generateMember();
            Mate jojo = fixtureGenerator.generateMate(meeting, member1);
            fixtureGenerator.generateEta(jojo);

            Member member2 = fixtureGenerator.generateMember();
            Mate coli = fixtureGenerator.generateMate(meeting, member2);
            fixtureGenerator.generateEta(coli);

            MateEtaRequest mateEtaMissingRequest = dtoGenerator.generateMateRequest(true);

            MateEtaResponsesV2 mateEtaResponses = RestAssured.given().log().all()
                    .header(HttpHeaders.AUTHORIZATION, fixtureGenerator.generateAccessTokenValueByMember(member1))
                    .body(mateEtaMissingRequest)
                    .contentType(ContentType.JSON)
                    .when()
                    .patch("/v2/meetings/1/mates/etas")
                    .then().log().all()
                    .statusCode(200)
                    .extract()
                    .as(MateEtaResponsesV2.class);

            assertThat(mateEtaResponses.mateEtas().get(0).status()).isEqualTo(EtaStatus.MISSING);
            assertThat(mateEtaResponses.mateEtas().get(1).status()).isNotEqualTo(EtaStatus.MISSING);
        }
    }
}

