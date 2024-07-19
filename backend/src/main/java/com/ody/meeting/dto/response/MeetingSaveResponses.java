package com.ody.meeting.dto.response;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record MeetingSaveResponses(

        @ArraySchema(schema = @Schema(implementation = MeetingSaveResponse.class))
        List<MeetingSaveResponse> meetings
) {

}
