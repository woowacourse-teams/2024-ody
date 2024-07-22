package com.ody.meeting.domain;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Location {

    private static final List<String> SUPPORT_REGION = List.of("서울", "경기", "인천");

    @NotNull
    private String address;

    @NotNull
    private String latitude;

    @NotNull
    private String longitude;

    public Location(String address, String latitude, String longitude) {
        validateSupportRegion(address);
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    private void validateSupportRegion(String address) {
        SUPPORT_REGION.stream()
                .filter(address::startsWith)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("현재 지원되지 않는 지역입니다."));
    }
}

