package com.ody.meeting.controller;

import com.ody.meeting.dto.request.MeetingSaveRequest;
import com.ody.meeting.dto.response.MeetingSaveResponse;
import com.ody.meeting.dto.response.MeetingSaveResponses;
import com.ody.member.domain.Member;
import com.ody.notification.dto.response.NotiLogFindResponses;
import com.ody.swagger.annotation.ErrorCode400;
import com.ody.swagger.annotation.ErrorCode401;
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
}
