package com.ody.meeting.dto.response;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;
import java.util.List;

public record MateEtaResponsesV2(

        @Schema(description = "요청자의 참여자 아이디", example = "4")
        long requesterMateId,

        @ArraySchema(schema = @Schema(implementation = MateEtaResponseV2.class))
        List<MateEtaResponseV2> mateEtas
) {

}
