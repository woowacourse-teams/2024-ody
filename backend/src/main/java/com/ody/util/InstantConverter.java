package com.ody.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InstantConverter {

    public static Instant kstToInstant(LocalDateTime dateTime) {
        return dateTime.toInstant(TimeUtil.KST_OFFSET);
    }

    public static ZonedDateTime instantToKst(Instant instant) {
        return instant.atZone(TimeUtil.KST_OFFSET);
    }
}
