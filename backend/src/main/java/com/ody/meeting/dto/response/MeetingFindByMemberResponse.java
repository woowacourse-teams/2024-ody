package com.ody.meeting.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public record MeetingFindByMemberResponse(
        @Schema(description = "약속 아이디", example = "1")
        Long id,

        @Schema(description = "약속 이름", example = "조조와 저녁 초밥")
        String name,

        @Schema(description = "약속 인원 수", example = "2")
        int mateCount,

        @Schema(description = "약속 날짜", example = "2024-09-10")
        String date,

        @Schema(description = "약속 시간", example = "13:30")
        String time,

        @Schema(description = "도착지 주소", example = "서울 테헤란로 411")
        String targetAddress,

        @Schema(description = "출발지 주소", example = "사당로22나길 22")
        String originAddress,

        @Schema(description = "소요 시간 (분)", example = "30")
        long durationMinutes
) {

    public MeetingFindByMemberResponse(
            Long id,
            String name,
            int mateCount,
            LocalDate date,
            LocalTime time,
            String targetAddress,
            String originAddress,
            long durationMinutes
    ) {
        this(
                id,
                name,
                mateCount,
                date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                time.format(DateTimeFormatter.ofPattern("HH:mm")),
                targetAddress,
                originAddress,
                durationMinutes
        );
    }
}
