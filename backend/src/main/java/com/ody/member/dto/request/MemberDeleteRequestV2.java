package com.ody.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record MemberDeleteRequestV2(
        @Schema(description = "애플 인증 코드", example = "authorizationcodeauthorizationcode")
        String authorizationCode
) {

}
