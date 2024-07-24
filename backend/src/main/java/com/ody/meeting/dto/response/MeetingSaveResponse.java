package com.ody.meeting.dto.response;

import com.ody.mate.domain.Mate;
import com.ody.meeting.domain.Meeting;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record MeetingSaveResponse(

        @Schema(description = "모임 ID", example = "1")
        Long id,

        @Schema(description = "모임 이름", example = "우테코 16조")
        String name,

        @Schema(description = "모임 날짜", example = "2024-07-15")
        LocalDate date,

        @Schema(description = "모임 시간", example = "14:00")
        LocalTime time,

        @Schema(description = "도착지 주소", example = "서울 송파구 올림픽로35다길 42")
        String targetAddress,

        @Schema(description = "도착지 위도", example = "37.515298")
        String targetLatitude,

        @Schema(description = "도착지 경도", example = "127.103113")
        String targetLongitude,

        @Schema(description = "모임 인원 수", example = "1")
        int mateCount,

        @Schema(description = "모임원 닉네임 목록", example = "[{\"nickname\": \"오디\"}]")
        List<MateResponse> mates,

        @Schema(description = "초대코드", example = "초대코드")
        String inviteCode
) {

    public static MeetingSaveResponse of(Meeting meeting, List<Mate> mates) {
        return new MeetingSaveResponse(
                meeting.getId(),
                meeting.getName(),
                meeting.getDate(),
                meeting.getTime(),
                meeting.getTarget().getAddress(),
                meeting.getTarget().getLatitude(),
                meeting.getTarget().getLongitude(),
                mates.size(),
                MateResponse.from(mates),
                meeting.getInviteCode()
        );
    }
}
