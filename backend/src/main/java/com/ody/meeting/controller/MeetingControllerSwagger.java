package com.ody.meeting.controller;

import com.ody.eta.dto.request.MateEtaRequest;
import com.ody.meeting.dto.request.MeetingSaveRequestV1;
import com.ody.meeting.dto.response.MateEtaResponsesV2;
import com.ody.meeting.dto.response.MeetingFindByMemberResponses;
import com.ody.meeting.dto.response.MeetingSaveResponseV1;
import com.ody.meeting.dto.response.MeetingWithMatesResponseV1;
import com.ody.meeting.dto.response.MeetingWithMatesResponseV2;
import com.ody.member.domain.Member;
import com.ody.notification.dto.response.NotiLogFindResponses;
import com.ody.swagger.annotation.ErrorCode400;
import com.ody.swagger.annotation.ErrorCode401;
import com.ody.swagger.annotation.ErrorCode404;
import com.ody.swagger.annotation.ErrorCode500;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

@Tag(name = "Meeting API")
@SecurityRequirement(name = "Authorization")
public interface MeetingControllerSwagger {

    @Operation(
            summary = "로그 목록 조회",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "로그 목록 조회 성공",
                            content = @Content(schema = @Schema(implementation = NotiLogFindResponses.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "존재하지 않은 약속이거나 약속 일원이 아닌 경우",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))
                    )
            }
    )
    @ErrorCode401
    @ErrorCode500
    ResponseEntity<NotiLogFindResponses> findAllMeetingLogs(@Parameter(hidden = true) Member member, Long meetingId);

    @Operation(
            summary = "약속 단건 조회 v1",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "약속 단건 조회 성공",
                            content = @Content(schema = @Schema(implementation = MeetingWithMatesResponseV1.class))
                    )
            }
    )
    @ErrorCode401
    @ErrorCode404(description = "존재하지 않는 약속이거나 해당 약속 참여자가 아닌 경우")
    @ErrorCode500
    ResponseEntity<MeetingWithMatesResponseV1> findMeetingWithMatesV1(
            @Parameter(hidden = true) Member member,
            Long meetingId
    );

    @Operation(
            summary = "약속 단건 조회 v2",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "약속 단건 조회 성공",
                            content = @Content(schema = @Schema(implementation = MeetingWithMatesResponseV2.class))
                    )
            }
    )
    @ErrorCode401
    @ErrorCode404(description = "존재하지 않는 약속이거나 해당 약속 참여자가 아닌 경우")
    @ErrorCode500
    ResponseEntity<MeetingWithMatesResponseV2> findMeetingWithMatesV2(
            @Parameter(hidden = true) Member member,
            Long meetingId
    );

    @Operation(
            summary = "내 약속 목록 조회",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "내 약속 목록 조회 성공",
                            content = @Content(schema = @Schema(implementation = MeetingFindByMemberResponses.class))
                    )
            }
    )
    @ErrorCode401
    @ErrorCode500
    ResponseEntity<MeetingFindByMemberResponses> findAllByMember(@Parameter(hidden = true) Member member);

    @Operation(
            summary = "초대코드 유효성 검사",
            responses = {
                    @ApiResponse(responseCode = "200", description = "유효한 초대 코드"),
            }
    )
    @ErrorCode400
    @ErrorCode401
    @ErrorCode404(description = "유효하지 않은 초대코드")
    @ErrorCode500
    ResponseEntity<Void> validateInviteCode(@Parameter(hidden = true) Member member, String inviteCode);

    @Operation(
            summary = "약속 개설",
            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = MeetingSaveRequestV1.class))),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "약속 개설 성공",
                            content = @Content(schema = @Schema(implementation = MeetingSaveResponseV1.class))
                    )
            }
    )
    @ErrorCode400
    @ErrorCode401
    @ErrorCode500
    ResponseEntity<MeetingSaveResponseV1> saveV1(
            @Parameter(hidden = true) Member member,
            MeetingSaveRequestV1 meetingSaveRequestV1
    );

    @Operation(
            summary = "약속 참여자 도착 현황 조회",
            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = MateEtaRequest.class))),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "약속 참여자 도착 현황 조회 성공",
                            content = @Content(schema = @Schema(implementation = MateEtaResponsesV2.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "클라이언트 입력 오류 또는 약속 시간 30분 전보다 이른 시간에 조회 시도 시",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))
                    )
            }
    )
    @ErrorCode401
    @ErrorCode404(description = "존재하지 않는 약속이거나 해당 약속 참여자가 아닌 경우")
    @ErrorCode500
    ResponseEntity<MateEtaResponsesV2> findAllMateEtasV2(
            @Parameter(hidden = true) Member member,
            Long meetingId,
            MateEtaRequest mateEtaRequest
    );
}
