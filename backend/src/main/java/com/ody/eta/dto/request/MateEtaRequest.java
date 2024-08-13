package com.ody.eta.dto.request;

import com.ody.common.annotation.SupportRegion;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;

@SupportRegion(latitudeFieldName = "currentLatitude", longitudeFieldName = "currentLongitude")
public record MateEtaRequest(

        @Schema(description = "위치추적 불가 여부", example = "false")
        boolean isMissing,

        @Schema(description = "현재 위도", example = "39.123345")
        @Nullable
        String currentLatitude,

        @Schema(description = "현재 경도", example = "126.234524")
        @Nullable
        String currentLongitude
) {

}
