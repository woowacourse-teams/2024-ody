package com.ody.mate.dto.request;

import com.ody.common.annotation.SupportRegion;
import com.ody.mate.domain.Mate;
import com.ody.meeting.domain.Location;
import com.ody.meeting.domain.Meeting;
import com.ody.member.domain.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@SupportRegion(latitudeFieldName = "originLatitude", longitudeFieldName = "originLongitude")
public record MateSaveRequestV2(

        @Schema(description = "초대코드", example = "inviteCodeinviteCode")
        @NotNull
        String inviteCode,

        @Schema(description = "출발지 주소", example = "서울 강남구 테헤란로 411")
        @NotNull
        String originAddress,

        @Schema(description = "출발지 위도", example = "37.505713")
        @NotNull
        String originLatitude,

        @Schema(description = "출발지 경도", example = "127.050691")
        @NotNull
        String originLongitude
) {

    public Location toOrigin() {
        return new Location(originAddress, originLatitude, originLongitude);
    }

    public Mate toMate(Meeting meeting, Member member, long estimatedMinutes) {
        return new Mate(meeting, member, toOrigin(), estimatedMinutes);
    }
}
