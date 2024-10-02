package com.ody.meeting.domain;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Location {

    @NotNull
    private String address;

    @NotNull
    @Embedded
    private Coordinates coordinates;

    public Location(String address, String latitude, String longitude) {
        this(address, new Coordinates(latitude, longitude));
    }

    public Location(String address, Coordinates coordinates) {
        this.address = address;
        this.coordinates = coordinates;
    }

    public String getLatitude() {
        return coordinates.getLatitude();
    }

    public String getLongitude() {
        return coordinates.getLongitude();
    }
}

