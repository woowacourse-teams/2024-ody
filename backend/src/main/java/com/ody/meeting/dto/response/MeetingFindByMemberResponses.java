package com.ody.meeting.dto.response;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record MeetingFindByMemberResponses(

        @ArraySchema(schema = @Schema(implementation = MeetingFindByMemberResponse.class))
        List<MeetingFindByMemberResponse> meetings
) {

}
