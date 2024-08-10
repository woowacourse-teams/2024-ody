package com.ody.eta.domain;

import com.ody.common.exception.OdyServerErrorException;
import com.ody.meeting.domain.Meeting;
import java.util.Arrays;
import java.util.function.BiPredicate;

public enum EtaStatus {

    MISSING((eta, meeting) -> eta.isMissing()),
    ARRIVED((eta, meeting) -> eta.isArrived()),
    ARRIVAL_SOON((eta, meeting) -> eta.isArrivalSoon(meeting) && !meeting.isEnd()),
    LATE_WARNING((eta, meeting) -> !eta.isArrivalSoon(meeting) && !meeting.isEnd()),
    LATE((eta, meeting) -> !eta.isArrivalSoon(meeting) && meeting.isEnd()),
    ;

    private final BiPredicate<Eta, Meeting> condition;

    EtaStatus(BiPredicate<Eta, Meeting> condition) {
        this.condition = condition;
    }

    public static EtaStatus of(Eta mateEta, Meeting meeting) {
        return Arrays.stream(values())
                .filter(status -> status.condition.test(mateEta, meeting))
                .findFirst()
                .orElseThrow(() -> new OdyServerErrorException("참여자의 ETA 상태를 판단할 수 없습니다"));
    }
}
