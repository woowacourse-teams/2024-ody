package com.ody.mate.dto.response;

import com.ody.meeting.domain.Meeting;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalTime;

public record MateSaveResponseV2(

        @Schema(description = "약속 ID", example = "1")
        Long meetingId,

        @Schema(description = "약속 날짜", type = "string", example = "2024-09-15")
        LocalDate date,

        @Schema(description = "약속 시간", type = "string", example = "14:00")
        LocalTime time
) {

    public static MateSaveResponseV2 from(Meeting meeting) {
        return new MateSaveResponseV2(meeting.getId(), meeting.getDate(), meeting.getTime());
    }
}
