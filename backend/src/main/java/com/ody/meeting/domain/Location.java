package com.ody.meeting.domain;

import com.ody.common.exception.OdyException;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Location {

    private static final List<String> SUPPORT_REGION = List.of("서울", "경기", "인천");
    private static final int MAX_DECIMAL_PLACES = 6;
    private static final BigDecimal MIN_LATITUDE = new BigDecimal("-90.0");
    private static final BigDecimal MAX_LATITUDE = new BigDecimal("90.0");
    private static final BigDecimal MIN_LONGITUDE = new BigDecimal("-180.0");
    private static final BigDecimal MAX_LONGITUDE = new BigDecimal("180.0");

    @NotNull
    private String address;

    @NotNull
    private String latitude;

    @NotNull
    private String longitude;

    public Location(String address, String latitude, String longitude) {
        validateSupportRegion(address);
        validateLatitude(latitude);
        validateLongitude(longitude);
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    private void validateSupportRegion(String address) {
        SUPPORT_REGION.stream()
                .filter(address::startsWith)
                .findAny()
                .orElseThrow(() -> new OdyException("현재 지원되지 않는 지역입니다."));
    }

    private void validateLatitude(String input) {
        validateCoordinateDecimalPlaces(input);
        try {
            BigDecimal latitude = new BigDecimal(input);
            if (latitude.compareTo(MIN_LATITUDE) < 0 || latitude.compareTo(MAX_LATITUDE) > 0) {
                throw new OdyException("위도는 -90부터 90까지의 범위가 가능합니다.");
            }
        } catch (NumberFormatException exception) {
            throw new OdyException("위도는 소수점 및 부호를 포함한 숫자이어야 합니다.");
        }
    }

    private void validateLongitude(String input) {
        validateCoordinateDecimalPlaces(input);
        try {
            BigDecimal longitude = new BigDecimal(input);
            if (longitude.compareTo(MIN_LONGITUDE) < 0 || longitude.compareTo(MAX_LONGITUDE) > 0) {
                throw new OdyException("경도는 -180부터 180까지의 범위가 가능합니다.");
            }
        } catch (NumberFormatException exception) {
            throw new OdyException("경도는 소수점 및 부호를 포함한 숫자이어야 합니다.");
        }
    }

    private void validateCoordinateDecimalPlaces(String input) {
        if (!input.contains(".")) {
            return;
        }
        String decimalPart = input.split("\\.")[1];
        if (decimalPart.length() > MAX_DECIMAL_PLACES) {
            throw new OdyException("좌표는 소수점 이하 최대 6자리까지 가능합니다.");
        }
    }
}

