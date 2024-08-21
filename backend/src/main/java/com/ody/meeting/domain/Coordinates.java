package com.ody.meeting.domain;

import com.ody.common.exception.OdyBadRequestException;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coordinates {

    @NotNull
    private String latitude;

    @NotNull
    private String longitude;

    public Coordinates(String latitude, String longitude) {
        this.latitude = formatForDoubleConversion(latitude);
        this.longitude = formatForDoubleConversion(longitude);
    }

    private String formatForDoubleConversion(String rawCoordinate) {
        try {
            BigDecimal coordinate = new BigDecimal(rawCoordinate);
            return coordinate.setScale(7, RoundingMode.HALF_UP).toString();
        } catch (NumberFormatException exception) {
            throw new OdyBadRequestException("double로 변환 불가능한 좌표입니다.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Coordinates that = (Coordinates) o;
        return Objects.equals(getLatitude(), that.getLatitude()) && Objects.equals(getLongitude(), that.getLongitude());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLatitude(), getLongitude());
    }
}
