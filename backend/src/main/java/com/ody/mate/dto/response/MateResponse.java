package com.ody.mate.dto.response;

import com.ody.mate.domain.Mate;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record MateResponse(

        @Schema(description = "참여자 닉네임", example = "오디")
        String nickname
) {

    public static List<MateResponse> from(List<Mate> mates) {
        return mates.stream()
                .map(mate -> new MateResponse(mate.getNicknameValue()))
                .toList();
    }
}
