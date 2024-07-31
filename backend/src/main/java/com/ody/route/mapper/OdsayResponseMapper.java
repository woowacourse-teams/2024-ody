package com.ody.route.mapper;

import com.ody.common.exception.OdyBadRequestException;
import com.ody.common.exception.OdyServerErrorException;
import com.ody.route.dto.OdsayResponse;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OdsayResponseMapper {

    private static final String CLOSE_LOCATION_CODE = "-98"; //출발지-도착지가 700m 이내일 때
    private static final String ODSAY_SERVER_ERROR = "500";
    private static final String EMPTY_MESSAGE = "";
    private static final long ZERO_TIME = 0L;

    public static long mapMinutes(OdsayResponse response) {
        checkOdsayException(response);

        if (isCloseLocation(response)) {
            return ZERO_TIME;
        }

        return response.minutes().orElseThrow(() -> {
            log.error("OdsayResponse minutes is Empty: {}", response);
            return new OdyServerErrorException("서버 에러");
        });
    }

    private static boolean isCloseLocation(OdsayResponse response) {
        Optional<String> code = response.code();
        return code.isPresent() && CLOSE_LOCATION_CODE.equals(code.get());
    }

    private static void checkOdsayException(OdsayResponse response) {
        if (response == null) {
            throw new OdyServerErrorException("response is null");
        }

        if (isServerErrorCode(response)) {
            log.error("ODsay 500 에러: {}", response);
            throw new OdyServerErrorException("서버 에러");
        }

        throw new OdyBadRequestException(
                response.message()
                .orElse(EMPTY_MESSAGE)
        );
    }

    private static boolean isServerErrorCode(OdsayResponse response) {
        Optional<String> code = response.code();
        return code.isPresent() && ODSAY_SERVER_ERROR.equals(code.get());
    }
}
