package com.ody.mate.dto.response;

import com.ody.mate.domain.Mate;
import com.ody.meeting.domain.Meeting;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalTime;

public record MateSaveResponse(

        @Schema(description = "약속 ID", example = "1")
        Long meetingId,

        @Schema(description = "약속 이름", example = "우테코 16조")
        String name,

        @Schema(description = "약속 날짜", type = "string", example = "2024-07-15")
        LocalDate date,

        @Schema(description = "약속 시간", type = "string", example = "14:00")
        LocalTime time,

        @Schema(description = "도착지 주소", example = "서울 송파구 올림픽로35다길 42")
        String targetAddress,

        @Schema(description = "도착지 위도", example = "37.515298")
        String targetLatitude,

        @Schema(description = "도착지 경도", example = "127.103113")
        String targetLongitude,

        @Schema(description = "초대코드", example = "초대코드")
        String inviteCode
) {

    public static MateSaveResponse from(Mate mate) {
        Meeting meeting = mate.getMeeting();
        return new MateSaveResponse(
                meeting.getId(),
                meeting.getName(),
                meeting.getDate(),
                meeting.getTime(),
                meeting.getTarget().getAddress(),
                meeting.getTarget().getLatitude(),
                meeting.getTarget().getLongitude(),
                meeting.getInviteCode()
        );
    }
}
