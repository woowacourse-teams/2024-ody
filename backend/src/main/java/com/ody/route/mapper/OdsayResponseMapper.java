package com.ody.route.mapper;

import com.ody.common.exception.OdyServerErrorException;
import com.ody.route.dto.OdsayResponse;
import java.util.Optional;
import java.util.OptionalLong;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OdsayResponseMapper {

    private static final String ROUTE_NOT_FOUND_CODE = "-98";
    private static final long ZERO_TIME = 0L;

    private OdsayResponseMapper() {
    }

    public static long getMinutes(OdsayResponse response) {
        Optional<String> code = response.code();
        Optional<String> message = response.code();
        OptionalLong minutes = response.minutes();

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
