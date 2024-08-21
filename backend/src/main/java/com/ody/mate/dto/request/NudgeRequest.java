package com.ody.mate.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record NudgeRequest(

        @Schema(description = "재촉한 사람", example = "1")
        @NotNull
        long requestMateId,

        @Schema(description = "재촉 당한 사람", example = "3")
        @NotNull
        long nudgedMateId
) {

}
