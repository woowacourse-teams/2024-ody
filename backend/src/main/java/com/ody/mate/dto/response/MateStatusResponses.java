package com.ody.mate.dto.response;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record MateStatusResponses(

        @ArraySchema(schema = @Schema(implementation = MateStatusResponse.class))
        List<MateStatusResponse> mateStatuses
) {

}
