package com.ody.mate.dto.request;

import com.ody.common.annotation.SupportRegion;
import com.ody.mate.domain.Mate;
import com.ody.mate.domain.Nickname;
import com.ody.meeting.domain.Location;
import com.ody.meeting.domain.Meeting;
import com.ody.member.domain.Member;
import io.swagger.v3.oas.annotations.media.Schema;

@SupportRegion(latitudeFieldName = "originLatitude", longitudeFieldName = "originLongitude")
public record MateSaveRequest(

        @Schema(description = "초대코드", example = "inviteCodeinviteCode")
        String inviteCode,

        @Schema(description = "참여자 닉네임", example = "오디")
        String nickname,

        @Schema(description = "출발지 주소", example = "서울 강남구 테헤란로 411")
        String originAddress,

        @Schema(description = "출발지 위도", example = "37.505713")
        String originLatitude,

        @Schema(description = "출발지 경도", example = "127.050691")
        String originLongitude
) {

    public Mate toMate(Meeting meeting, Member member, long estimatedMinutes) {
        Location origin = new Location(originAddress, originLatitude, originLongitude);
        return new Mate(meeting, member, new Nickname(nickname), origin, estimatedMinutes);
    }
}
