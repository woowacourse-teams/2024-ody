package com.ody.meeting.controller;

import com.ody.mate.dto.request.MateEtaRequest;
import com.ody.mate.dto.response.MateEtaResponses;
import com.ody.meeting.dto.request.MeetingSaveRequest;
import com.ody.meeting.dto.response.MeetingSaveResponse;
import com.ody.meeting.dto.response.MeetingSaveResponses;
import com.ody.meeting.dto.response.MeetingWithMatesResponse;
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
            summary = "모임 개설",
            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = MeetingSaveRequest.class))),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "모임 개설 성공",
                            content = @Content(schema = @Schema(implementation = MeetingSaveResponse.class))
                    )
            }
    )
    @ErrorCode400
    @ErrorCode401
    @ErrorCode500
    ResponseEntity<MeetingSaveResponse> save(
            @Parameter(hidden = true) Member member,
            MeetingSaveRequest meetingSaveRequest
    );

    @Operation(
            summary = "약속 단건 조회",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "약속 단건 조회 성공",
                            content = @Content(schema = @Schema(implementation = MeetingWithMatesResponse.class))
                    )
            }
    )
    @ErrorCode401
    @ErrorCode404(description = "존재하지 않는 약속이거나 해당 약속 참여자가 아닌 경우")
    @ErrorCode500
    ResponseEntity<MeetingWithMatesResponse> findMeetingWithMates(
            @Parameter(hidden = true) Member member,
            Long meetingId
    );

    @Operation(
            summary = "참여중인 모임 목록 조회",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "모임 목록 조회 성공",
                            content = @Content(schema = @Schema(implementation = MeetingSaveResponses.class))
                    )
            }
    )
    @ErrorCode401
    @ErrorCode500
    ResponseEntity<MeetingSaveResponses> findMine(@Parameter(hidden = true) Member member);

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
                            description = "존재하지 않은 모임방이거나 모임방 일원이 아닌 경우",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))
                    )
            }
    )
    @ErrorCode401
    @ErrorCode500
    ResponseEntity<NotiLogFindResponses> findAllMeetingLogs(@Parameter(hidden = true) Member member, Long meetingId);

    @Operation(
            summary = "초대코드 유효성 검사",
            responses = {
                    @ApiResponse(responseCode = "200", description = "유효한 초대 코드"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "유효하지 않은 초대코드",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))
                    )
            }
    )
    @ErrorCode400
    @ErrorCode401
    @ErrorCode500
    ResponseEntity<Void> validateInviteCode(@Parameter(hidden = true) Member member, String inviteCode);

    @Operation(
            summary = "약속 참여자 eta 상태 목록 조회",
            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = MateEtaRequest.class))),

            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "약속 참여자 eta 상태 목록 조회 성공",
                            content = @Content(schema = @Schema(implementation = MateEtaResponses.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "클라이언트 입력 오류 또는 약속 시간 30분 전보다 이른 시간에 조회 시도 시",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "존재하지 않는 모임이거나 해당 모임 참여자가 아닌 경우",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))
                    )
            }
    )
    @ErrorCode401
    @ErrorCode500
    ResponseEntity<MateEtaResponses> findAllMateEtas(
            @Parameter(hidden = true) Member member,
            Long meetingId,
            MateEtaRequest mateEtaRequest
    );
}
