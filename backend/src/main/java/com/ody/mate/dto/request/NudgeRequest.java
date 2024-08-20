package com.ody.mate.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record NudgeRequest(

        @Schema(description = "재촉한 사람", example = "1")
        Long requestMateId,

        @Schema(description = "재촉 당한 사람", example = "3")
        Long nudgedMateId
) {

}
