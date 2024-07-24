package com.ody.member.controller;

import com.ody.member.domain.Member;
import com.ody.swagger.annotation.DeviceTokenHeader;
import com.ody.swagger.annotation.ErrorCode500;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Member API")
public interface MemberControllerSwagger {

    @Operation(
            summary = "회원 추가",
            responses = @ApiResponse(responseCode = "201", description = "회원 추가 성공")
    )
    @DeviceTokenHeader
    @ErrorCode500
    ResponseEntity<Void> save(@Parameter(hidden = true) Member member);
}
