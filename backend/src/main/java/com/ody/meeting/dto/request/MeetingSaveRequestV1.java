package com.ody.meeting.dto.request;

import com.ody.common.annotation.FutureOrPresentDateTime;
import com.ody.meeting.domain.Location;
import com.ody.meeting.domain.Meeting;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;

@FutureOrPresentDateTime(dateFieldName = "date", timeFieldName = "time")
public record MeetingSaveRequestV1(

        @Schema(description = "모임 이름", example = "우테코 16조")
        @Size(min = 1, max = 15, message = "약속 이름은 1글자 이상, 16자 미만으로 입력 가능합니다.")
        String name,

        @Schema(description = "모임 날짜", type = "string", example = "2024-07-15")
        LocalDate date,

        @Schema(description = "모임 시간", type = "string", example = "14:00")
        LocalTime time,

        @Schema(description = "도착지 주소", example = "서울 송파구 올림픽로35다길 42")
        String targetAddress,

        @Schema(description = "도착지 위도", example = "37.515298")
        String targetLatitude,

        @Schema(description = "도착지 경도", example = "127.103113")
        String targetLongitude
) {

    public Meeting toMeeting(String inviteCode) {
        Location target = new Location(targetAddress, targetLatitude, targetLongitude);
        return new Meeting(name, date, time, target, inviteCode);
    }
}
