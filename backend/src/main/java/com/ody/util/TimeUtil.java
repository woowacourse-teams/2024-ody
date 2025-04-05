package com.ody.util;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TimeUtil {

    private static final int ROUND_DIGITS = 0;

    public static final ZoneOffset KST_OFFSET = ZoneOffset.ofHours(9);

    public static LocalDateTime nowWithTrim() {
        return trimSecondsAndNanos(LocalDateTime.now());
    }

    public static LocalDateTime trimSecondsAndNanos(LocalDateTime time) {
        return time.withSecond(ROUND_DIGITS)
                .withNano(ROUND_DIGITS);
    }

    public static LocalTime trimSecondsAndNanos(LocalTime time) {
        return time.withSecond(ROUND_DIGITS)
                .withNano(ROUND_DIGITS);
    }

    public static boolean isPast(LocalDateTime dateTime) {
        return nowWithTrim().isAfter(dateTime);
    }

    public static boolean isUpcoming(LocalDateTime dateTime) {
        return dateTime.isAfter(nowWithTrim());
    }
}
