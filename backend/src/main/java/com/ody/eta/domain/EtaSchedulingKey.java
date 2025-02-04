package com.ody.eta.domain;

import com.ody.common.exception.OdyServerErrorException;
import com.ody.mate.domain.Mate;
import java.time.LocalDateTime;
import java.util.StringTokenizer;

public record EtaSchedulingKey(
        String deviceToken,
        long meetingId,
        LocalDateTime meetingDateTime
) {

    private static final String DELIMITER = "/";

    public static EtaSchedulingKey from(Mate mate) {
        return new EtaSchedulingKey(
                mate.getMember().getDeviceToken().getValue(),
                mate.getMeeting().getId(),
                LocalDateTime.of(mate.getMeeting().getDate(), mate.getMeeting().getTime())
        );
    }

    public static EtaSchedulingKey from(String key) {
        StringTokenizer tokenizer = new StringTokenizer(key, DELIMITER);
        if (tokenizer.countTokens() != 3) {
            throw new OdyServerErrorException("유효하지 않은 EtaSchedulingKey 입니다.");
        }
        return new EtaSchedulingKey(
                tokenizer.nextToken(),
                Long.parseLong(tokenizer.nextToken()),
                LocalDateTime.parse(tokenizer.nextToken())
        );
    }

    public String serialize() {
        return deviceToken + DELIMITER
                + meetingId + DELIMITER
                + meetingDateTime;
    }
}
