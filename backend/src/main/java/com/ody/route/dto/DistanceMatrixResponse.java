package com.ody.route.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.springframework.data.geo.Distance;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DistanceMatrixResponse(
        @JsonProperty("destination_addresses")
        List<String> destinationAddresses,

        @JsonProperty("origin_addresses")
        List<String> originAddresses,

        List<Row> rows,

        String status,

        @JsonProperty("error_message")
        String errorMessage
) {

    public record Row(List<Element> elements) {}

    public record Element(Distance distance, Duration duration, String status) {}

    public record Duration(String text, int value) {}
}
