package com.ody.meeting.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.ody.common.BaseControllerTest;
import com.ody.common.Fixture;
import com.ody.eta.domain.Eta;
import com.ody.eta.domain.EtaStatus;
import com.ody.eta.dto.request.MateEtaRequest;
import com.ody.eta.repository.EtaRepository;
import com.ody.mate.domain.Mate;
import com.ody.mate.domain.Nickname;
import com.ody.mate.repository.MateRepository;
import com.ody.meeting.domain.Location;
import com.ody.meeting.domain.Meeting;
import com.ody.meeting.dto.request.MeetingSaveRequestV1;
import com.ody.meeting.dto.response.MateEtaResponseV2;
import com.ody.meeting.dto.response.MateEtaResponsesV2;
import com.ody.meeting.dto.response.MeetingFindByMemberResponses;
import com.ody.meeting.dto.response.MeetingSaveResponseV1;
import com.ody.meeting.repository.MeetingRepository;
import com.ody.member.domain.Member;
import com.ody.member.repository.MemberRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.regex.Pattern;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

class MeetingControllerTest extends BaseControllerTest {

    private static String LOCALTIME_FORMAT = "HH:mm";

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private MateRepository mateRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private EtaRepository etaRepository;

    @DisplayName("모임 개설 성공 시, 201을 응답한다")
    @Test
    void saveV1() {
        String authorization = saveMember();

        MeetingSaveRequestV1 meetingRequest = new MeetingSaveRequestV1(
                "우테코 16조",
                LocalDate.now().plusDays(1),
                LocalTime.now().plusHours(1),
                "서울 송파구 올림픽로35다길 42",
                "37.515298",
                "127.103113"
        );

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header(HttpHeaders.AUTHORIZATION, authorization)
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
        Member member = memberRepository.save(Fixture.MEMBER1);
        Meeting odyMeeting = meetingRepository.save(Fixture.ODY_MEETING);
        meetingRepository.save(odyMeeting);
        mateRepository.save(new Mate(odyMeeting, member, new Nickname("제리"), Fixture.ORIGIN_LOCATION, 10L));

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
        Location origin = Fixture.ORIGIN_LOCATION;
        Member member = memberRepository.save(Fixture.MEMBER1);
        Meeting meeting = meetingRepository.save(Fixture.ODY_MEETING);
        mateRepository.save(new Mate(meeting, member, new Nickname("은별"), origin, 10L));

        RestAssured.given()
                .log()
                .all()
                .header(HttpHeaders.AUTHORIZATION, fixtureGenerator.generateAccessTokenValueByMember(member))
                .when()
                .get("/meetings/1/noti-log")
                .then()
                .log()
                .all()
                .statusCode(200);
    }

    @DisplayName("유효하지 않은 초대 코드일 경우 404 에러를 반환한다.")
    @Test
    void validateInviteCode() {
        Member member = memberRepository.save(Fixture.MEMBER1);

        RestAssured.given()
                .header(HttpHeaders.AUTHORIZATION, fixtureGenerator.generateAccessTokenValueByMember(member))
                .when()
                .get("/invite-codes/testcode/validate")
                .then()
                .log()
                .all()
                .statusCode(404);
    }

    @DisplayName("Eta API 테스트")
    @Nested
    class EtaTest {

        @DisplayName("참여자들의 위치 현황 목록 성공하면 200응답 반환한다")
        @Test
        void findAllMateEtas() {
            Location origin = Fixture.ORIGIN_LOCATION;
            Member member = memberRepository.save(Fixture.MEMBER1);
            Meeting meeting = meetingRepository.save(Fixture.ODY_MEETING);
            Mate mate = mateRepository.save(new Mate(meeting, member, new Nickname("은별"), origin, 10L));
            etaRepository.save(new Eta(mate, 10L));

            MateEtaRequest mateEtaRequest = new MateEtaRequest(false, origin.getLatitude(), origin.getLongitude());

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
            Location origin = Fixture.ORIGIN_LOCATION;
            Member member = memberRepository.save(Fixture.MEMBER1);
            Meeting meeting = meetingRepository.save(Fixture.ODY_MEETING);
            Mate mate = mateRepository.save(new Mate(meeting, member, new Nickname("은별"), origin, 10L));
            etaRepository.save(new Eta(mate, 10L));

            MateEtaRequest mateEtaMissingRequest = new MateEtaRequest(
                    true,
                    origin.getLatitude(),
                    origin.getLongitude()
            );
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

            MateEtaRequest mateEtaNotMissingRequest = new MateEtaRequest(
                    false,
                    origin.getLatitude(),
                    origin.getLongitude()
            );
            MateEtaResponseV2 mateEtaNotMissingResponse = RestAssured.given()
                    .log()
                    .all()
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
            Location origin = Fixture.ORIGIN_LOCATION;
            Meeting meeting = meetingRepository.save(Fixture.ODY_MEETING);

            Member member1 = memberRepository.save(Fixture.MEMBER1);
            Mate jojo = mateRepository.save(new Mate(meeting, member1, new Nickname("은별"), origin, 10L));
            etaRepository.save(new Eta(jojo, 10L));

            Member member2 = memberRepository.save(Fixture.MEMBER2);
            Mate coli = mateRepository.save(new Mate(meeting, member2, new Nickname("콜리"), origin, 10L));
            etaRepository.save(new Eta(coli, 10L));

            MateEtaRequest mateEtaMissingRequest = new MateEtaRequest(
                    true,
                    origin.getLatitude(),
                    origin.getLongitude()
            );
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

