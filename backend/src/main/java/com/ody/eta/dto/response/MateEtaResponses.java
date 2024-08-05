package com.ody.eta.dto.response;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record MateEtaResponses(

        @ArraySchema(schema = @Schema(implementation = MateEtaResponse.class))
        List<MateEtaResponse> mateEtas
) {

}
