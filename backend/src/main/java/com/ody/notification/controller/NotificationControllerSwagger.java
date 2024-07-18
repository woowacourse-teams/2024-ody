package com.ody.notification.controller;

import com.ody.notification.dto.response.NotiLogFindResponse;
import com.ody.swagger.annotation.ErrorCode401;
import com.ody.swagger.annotation.ErrorCode500;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;


@Tag(name = "Notification API")
public interface NotificationControllerSwagger {

    @Operation(
            summary = "로그 목록 조회",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "로그 목록 조회 성공",
                            content = @Content(schema = @Schema(implementation = NotiLogFindResponse.class))
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
    ResponseEntity<NotiLogFindResponse> findAllByMeetingId(String fcmToken, Long meetingId);
}
