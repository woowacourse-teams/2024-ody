package com.ody.route.mapper;

import com.ody.common.exception.OdyBadRequestException;
import com.ody.common.exception.OdyServerErrorException;
import com.ody.route.dto.OdsayResponse;
import java.util.Optional;
import java.util.OptionalLong;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OdsayResponseMapper {

    private static final String CLOSE_LOCATION_CODE = "-98"; //출발지-도착지가 700m 이내일 때
    private static final String ODSAY_SERVER_ERROR = "500";
    private static final String EMPTY_MESSAGE = "";
    private static final long ZERO_TIME = 0L;

    private OdsayResponseMapper() {
    }

    public static long mapMinutes(OdsayResponse response) {
        Optional<String> code = response.code();
        OptionalLong minutes = response.minutes();

        if (code.isPresent()) {
            checkOdsayCode(response);
        }

        if (isCloseLocation(code)) {
            return ZERO_TIME;
        }

        return minutes.orElseThrow(() -> {
            log.error("OdsayResponse minutes is Empty: {}", response);
            return new OdyServerErrorException("서버 에러");
        });
    }

    private static boolean isCloseLocation(Optional<String> code) {
        return code.isPresent() && CLOSE_LOCATION_CODE.equals(code.get());
    }

    private static void checkOdsayCode(OdsayResponse response) {
        Optional<String> code = response.code();
        Optional<String> message = response.message();

        if (code.isEmpty() || ODSAY_SERVER_ERROR.equals(code.get())) {
            log.error("ODsay 500 에러: {}", response);
            throw new OdyServerErrorException("서버 에러");
        }

        throw new OdyBadRequestException(message.orElse(EMPTY_MESSAGE));
    }
}
