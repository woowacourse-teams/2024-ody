package com.ody.member.controller;

import com.ody.member.domain.Member;
import com.ody.swagger.annotation.ErrorCode401;
import com.ody.swagger.annotation.ErrorCode403;
import com.ody.swagger.annotation.ErrorCode500;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Member API")
@SecurityRequirement(name = "Authorization")
public interface MemberControllerSwagger {

    @Operation(
            summary = "회원 삭제",
            responses = @ApiResponse(responseCode = "204", description = "회원 삭제 성공")
    )
    @ErrorCode401
    @ErrorCode500
    ResponseEntity<Void> delete(@Parameter(hidden = true) Member member);

    @Operation(
            summary = "회원 삭제",
            responses = @ApiResponse(responseCode = "204", description = "회원 삭제 성공")
    )
    @ErrorCode401
    @ErrorCode403
    @ErrorCode500
    ResponseEntity<Void> deleteV2(@Parameter(hidden = true) Member member);
}
