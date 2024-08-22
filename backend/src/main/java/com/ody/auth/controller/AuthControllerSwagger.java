package com.ody.auth.controller;

import com.ody.auth.dto.request.AuthRequest;
import com.ody.auth.dto.response.AuthResponse;
import com.ody.member.domain.Member;
import com.ody.swagger.annotation.ErrorCode400;
import com.ody.swagger.annotation.ErrorCode500;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

@Tag(name = "Auth API")
@SecurityRequirement(name = "Authorization")
public interface AuthControllerSwagger {

    @Operation(
            summary = "카카오 소셜 로그인",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "카카오 소셜 로그인 성공",
                            content = @Content(schema = @Schema(implementation = AuthResponse.class))
                    )
            }
    )
    @ErrorCode400
    @ErrorCode500
    ResponseEntity<AuthResponse> authKakao(AuthRequest authRequest);

    @Operation(
            summary = "액세스 토큰 갱신",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "액세스 토큰 갱신 성공",
                            content = @Content(schema = @Schema(implementation = AuthResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "만료된 리프레시 토큰",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))
                    ),
            }
    )
    @ErrorCode400
    @ErrorCode500
    ResponseEntity<AuthResponse> refreshAccessToken(@Parameter(hidden = true) String authorization);
}
