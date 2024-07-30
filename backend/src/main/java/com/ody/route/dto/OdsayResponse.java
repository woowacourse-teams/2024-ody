package com.ody.route.dto;

import com.ody.common.exception.OdyServerErrorException;
import java.util.Optional;
import java.util.OptionalLong;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class OdsayResponse {

    private static final String ROUTE_NOT_FOUND_CODE = "-98";
    private static final long ZERO_TIME = 0L;

    private final Optional<String> code;
    private final Optional<String> message;
    private final OptionalLong minutes;

    public long getMinutes() {
        if (code.isPresent() && ROUTE_NOT_FOUND_CODE.equals(code.get())) {
            return ZERO_TIME;
        }

        if (minutes.isPresent()) {
            return minutes.getAsLong();
        }

        log.error("ODsay 에러: ", message.get());
        throw new OdyServerErrorException("ODsay 서버 에러 : minutes is null");
    }
}
