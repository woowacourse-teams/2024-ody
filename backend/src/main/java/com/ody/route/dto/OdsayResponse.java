package com.ody.route.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ody.route.mapper.OdsayResponseDeserializer;
import java.util.Optional;
import java.util.OptionalLong;

@JsonDeserialize(using = OdsayResponseDeserializer.class)
public record OdsayResponse(
        Optional<String> code,
        Optional<String> message,
        OptionalLong minutes
) {

}
