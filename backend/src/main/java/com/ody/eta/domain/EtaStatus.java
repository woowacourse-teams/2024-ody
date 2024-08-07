package com.ody.eta.domain;

import com.ody.common.exception.OdyServerErrorException;
import java.time.LocalDateTime;

public enum EtaStatus {
    ARRIVED,
    ARRIVAL_SOON,
    LATE_WARNING,
    LATE,
    MISSING,
    ;

    public static EtaStatus from(Eta mateEta, LocalDateTime meetingTime, LocalDateTime now, boolean isMissing) {
        if (isMissing) {
            return MISSING;
        }
        if (mateEta.isArrived()) {
            return ARRIVED;
        }

        if (!mateEta.willBeLate(meetingTime) && (now.isBefore(meetingTime))) {
            return ARRIVAL_SOON;
        }

        if (mateEta.willBeLate(meetingTime)) {
            if (now.isBefore(meetingTime)) {
                return LATE_WARNING;
            }
            return LATE;
        }

        throw new OdyServerErrorException("참여자의 ETA 상태를 판단할 수 없습니다");
    }
}
