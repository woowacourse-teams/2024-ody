package com.ody.mate.controller;

import com.ody.mate.dto.request.MateSaveRequest;
import com.ody.meeting.dto.response.MeetingSaveResponse;
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

@Tag(name = "Mate API")
public interface MateControllerSwagger {

    @Operation(
            summary = "모임 참여",
            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = MateSaveRequest.class))),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "모임 참여 성공",
                            content = @Content(schema = @Schema(implementation = MeetingSaveResponse.class))
                    ),
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
    ResponseEntity<MeetingSaveResponse> save(String fcmToken, MateSaveRequest mateSaveRequest);
}
