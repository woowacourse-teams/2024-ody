package com.ody.mate.controller;

import com.ody.mate.dto.request.MateSaveRequest;
import com.ody.meeting.dto.response.MeetingSaveResponse;
import com.ody.member.domain.Member;
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
import org.springframework.http.ResponseEntity;

@Tag(name = "Mate API")
@SecurityRequirement(name = "Authorization")
public interface MateControllerSwagger {

    @Operation(
            summary = "모임 참여",
            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = MateSaveRequest.class))),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "모임 참여 성공",
                            content = @Content(schema = @Schema(implementation = MeetingSaveResponse.class))
                    )
            }
    )
    @ErrorCode400
    @ErrorCode401
    @ErrorCode404(description = "유효하지 않은 초대코드")
    @ErrorCode500
    ResponseEntity<MeetingSaveResponse> saveV1(@Parameter(hidden = true) Member member, MateSaveRequest mateSaveRequest);
}
