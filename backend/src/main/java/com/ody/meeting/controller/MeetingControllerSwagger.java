package com.ody.meeting.controller;

import com.ody.meeting.dto.MeetingSaveRequest;
import com.ody.meeting.dto.MeetingSaveResponse;
import com.ody.swagger.annotation.ErrorCode400;
import com.ody.swagger.annotation.ErrorCode401;
import com.ody.swagger.annotation.ErrorCode500;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

@Tag(name = "Meeting API")
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
    ResponseEntity<MeetingSaveResponse> save(String fcmToken, MeetingSaveRequest meetingSaveRequest);

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
    ResponseEntity<Void> validateInviteCode(String fcmToken, String inviteCode);
}
