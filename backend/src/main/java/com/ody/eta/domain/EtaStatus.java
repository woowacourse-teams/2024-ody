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

    public static EtaStatus from(
            long countdownMinutes,
            LocalDateTime meetingTime,
            boolean isArrived,
            boolean isMissing
    ) {

        if (isMissing) {
            return MISSING;
        }
        if (isArrived) {
            return ARRIVED;
        }

        LocalDateTime eta = LocalDateTime.now().withSecond(0).withNano(0).plusMinutes(countdownMinutes);
        LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);

        if (eta.isAfter(meetingTime) && now.isBefore(meetingTime)) {
            return LATE_WARNING;
        }

        if (eta.isAfter(meetingTime) && (now.isAfter(meetingTime) || now.isEqual(meetingTime))) {
            return LATE;
        }

        if ((eta.isBefore(meetingTime) || eta.isEqual(meetingTime)) && (now.isBefore(meetingTime))) {
            return ARRIVAL_SOON;
        }

        throw new OdyServerErrorException("참여자의 ETA 상태를 판단할 수 없습니다");
    }

    public static EtaStatus from2(
            Eta mateEta,
            LocalDateTime meetingTime,
            LocalDateTime now,
            boolean isMissing
    ) {
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
