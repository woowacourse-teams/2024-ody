package com.ody.mate.dto.request;

import com.ody.mate.domain.Mate;
import com.ody.mate.domain.NickName;
import com.ody.meeting.domain.Location;
import com.ody.meeting.domain.Meeting;
import com.ody.member.domain.Member;
import io.swagger.v3.oas.annotations.media.Schema;

public record MateSaveRequest(

        @Schema(description = "초대코드", example = "초대코드")
        String inviteCode,

        @Schema(description = "참여자 닉네임", example = "제리")
        String nickname,

        @Schema(description = "출발지 주소", example = "서울 강남구 테헤란로 411")
        String originAddress,

        @Schema(description = "출발지 위도", example = "37.505713")
        String originLatitude,

        @Schema(description = "출발지 경도", example = "127.050691")
        String originLongitude
) {

    public Mate toMate(Meeting meeting, Member member) {
        Location origin = new Location(originAddress, originLatitude, originLongitude);
        return new Mate(meeting, member, new NickName(nickname), origin);
    }
}
