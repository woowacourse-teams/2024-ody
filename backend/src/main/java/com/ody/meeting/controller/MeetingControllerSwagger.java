package com.ody.meeting.controller;

import com.ody.meeting.dto.MeetingRequest;
import com.ody.meeting.dto.MeetingResponse;
import com.ody.swagger.annotation.ErrorCode400;
import com.ody.swagger.annotation.ErrorCode401;
import com.ody.swagger.annotation.ErrorCode500;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Meeting API")
public interface MeetingControllerSwagger {

    @Operation(
            summary = "모임 개설",
            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = MeetingRequest.class))),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "모임 개설 성공",
                            content = @Content(schema = @Schema(implementation = MeetingResponse.class))
                    )
            }
    )
    @ErrorCode400
    @ErrorCode401
    @ErrorCode500
    ResponseEntity<MeetingResponse> save(String fcmToken, MeetingRequest meetingRequest);
}
