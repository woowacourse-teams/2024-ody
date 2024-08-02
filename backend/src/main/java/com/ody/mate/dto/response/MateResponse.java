package com.ody.mate.dto.response;

import com.ody.mate.domain.Mate;
import java.util.List;

public record MateResponse(String nickname) {

    public static List<MateResponse> from(List<Mate> mates) {
        return mates.stream()
                .map(mate -> new MateResponse(mate.getNicknameValue()))
                .toList();
    }
}
