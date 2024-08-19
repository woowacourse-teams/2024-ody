package com.ody.meeting.dto.response;

import com.ody.eta.domain.EtaStatus;
import io.swagger.v3.oas.annotations.media.Schema;

public record MateEtaResponseV2(

        @Schema(description = "참여자 아이디", example = "3")
        long mateId,

        @Schema(description = "참여자 닉네임", example = "콜리")
        String nickname,

        @Schema(description = "참여자 ETA에 따른 상태", example = "LATE_WARNING")
        EtaStatus status,

        @Schema(description = "도착지까지 남은 소요시간(분)", example = "32")
        long durationMinutes
) {

}
