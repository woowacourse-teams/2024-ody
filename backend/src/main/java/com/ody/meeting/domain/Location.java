package com.ody.meeting.domain;

import com.ody.common.exception.OdyBadRequestException;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Location {

    private static final List<String> SUPPORT_REGION = List.of("서울", "경기", "인천");

    @NotNull
    private String address;

    @NotNull
    @Embedded
    private Coordinates coordinates;

    public Location(String address, String latitude, String longitude) {
        this(address, new Coordinates(latitude, longitude));
    }

    public Location(String address, Coordinates coordinates) {
        validateSupportRegion(address);
        this.address = address;
        this.coordinates = coordinates;
    }

    private void validateSupportRegion(String address) {
        SUPPORT_REGION.stream()
                .filter(address::startsWith)
                .findAny()
                .orElseThrow(() -> new OdyBadRequestException("현재 지원되지 않는 지역입니다."));
    }

    public String getLatitude() {
        return coordinates.getLatitude();
    }

    public String getLongitude() {
        return coordinates.getLongitude();
    }
}

