package com.ody.eta.domain;

import com.ody.common.exception.OdyServerErrorException;
import com.ody.route.domain.RouteTime;
import java.time.LocalDateTime;

public enum EtaStatus {
    ARRIVED,
    ARRIVAL_SOON,
    LATE_WARNING,
    LATE,
    MISSING,
    ;

    public static EtaStatus from(
            long countdownMinutes,
            Eta mateEta,
            LocalDateTime meetingTime,
            boolean isMissing
    ) {

        if (isMissing) {
            return MISSING;
        }

        LocalDateTime eta = LocalDateTime.now().plusMinutes(countdownMinutes);

        if (eta.isAfter(meetingTime) && LocalDateTime.now().isBefore(meetingTime)) {
            return LATE_WARNING;
        }

        if (eta.isAfter(meetingTime)
                && (LocalDateTime.now().isAfter(meetingTime) || LocalDateTime.now().isEqual(meetingTime))) {
            return LATE;
        }

        if (mateEta.isArrived()
                && (LocalDateTime.now().isBefore(meetingTime) || LocalDateTime.now().isEqual(meetingTime))) {
            return ARRIVED;
        }

        if ((eta.isBefore(meetingTime) || eta.isEqual(meetingTime))
                && (LocalDateTime.now().isBefore(meetingTime) || LocalDateTime.now().isEqual(meetingTime))) {
            return ARRIVAL_SOON;
        }

        throw new OdyServerErrorException("참여자의 ETA 상태를 판단할 수 없습니다");
    }
}
