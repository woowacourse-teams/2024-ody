package com.ody.eta.dto.response;

import com.ody.eta.domain.Eta;
import com.ody.eta.domain.EtaStatus;
import com.ody.meeting.domain.Meeting;
import io.swagger.v3.oas.annotations.media.Schema;

public record MateEtaResponse(

        @Schema(description = "참여자 닉네임", example = "콜리")
        String nickname,

        @Schema(description = "참여자 ETA에 따른 상태", example = "LATE_WARNING")
        EtaStatus status,

        @Schema(description = "도착지까지 남은 소요시간(분)", example = "32")
        long durationMinutes
) {

    public static MateEtaResponse of(Eta eta, Meeting meeting) {
        return new MateEtaResponse(
                eta.getMate().getNicknameValue(),
                EtaStatus.of(eta, meeting),
                mapMinutes(eta, meeting)
        );
    }

    // 안드측과 협의해서 추후에 View 로직에서 처리 가능한 부분으로 사라질 메서드임 (DTO에 로직이 있는거 걱정 ㄴㄴ)
    private static long mapMinutes(Eta eta, Meeting meeting) {
        if (eta.isMissing()) {
            return -1L;
        }
        if (eta.isArrivalSoon(meeting)) {
            return 1L;
        }
        return eta.countDownMinutes();
    }
}
