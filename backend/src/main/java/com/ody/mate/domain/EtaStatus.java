package com.ody.mate.domain;

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
            long remainingDuration,
            LocalDateTime meetingTime,
            double meter,
            boolean isMissing
    ) {
        if (isMissing) {
            return MISSING;
        }

        LocalDateTime eta = LocalDateTime.now().plusMinutes(remainingDuration);

        if (eta.isAfter(meetingTime) && LocalDateTime.now().isBefore(meetingTime)) {
            return LATE_WARNING;
        }

        if (eta.isAfter(meetingTime)
                && (LocalDateTime.now().isAfter(meetingTime) || LocalDateTime.now().isEqual(meetingTime))) {
            return LATE;
        }

        if (meter <= 300
                && (LocalDateTime.now().isBefore(meetingTime) || LocalDateTime.now().isEqual(meetingTime))) {
            return ARRIVED;
        }

        if ((meter > 300 && meter <= 700)
                && (LocalDateTime.now().isBefore(meetingTime) || LocalDateTime.now()
                .isEqual(meetingTime))) {
            return ARRIVAL_SOON;
        }

        // 충분히 도착가능함을 표현할 수 있는 상수가 있어야함 .. : 700미터 이상 + 약속 시간 내 도착 가능.

        throw new OdyServerErrorException("참여자의 ETA 상태를 판단할 수 없습니다");
    }
}
