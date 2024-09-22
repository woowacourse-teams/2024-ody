package com.ody.route.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DistanceMatrixResponse(

        @JsonProperty("destination_addresses")
        List<String> destinationAddresses,

        @JsonProperty("origin_addresses")
        List<String> originAddresses,

        List<DistanceMatrixRow> rows,

        DistanceMatrixStatus status,

        @JsonProperty("error_message")
        String errorMessage
) {

    public record DistanceMatrixRow(List<DistanceMatrixElement> elements) {}

    public record DistanceMatrixElement(DistanceMatrixElementStatus status, TextValueObject duration) {}

    public record TextValueObject(String text, int value) {}
}
