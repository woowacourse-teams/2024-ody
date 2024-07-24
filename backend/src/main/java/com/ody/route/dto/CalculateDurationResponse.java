package com.ody.route.dto;

import java.util.Optional;
import java.util.OptionalLong;

public record CalculateDurationResponse(

        Optional<String> code,
        Optional<String> message,
        OptionalLong minutes
) {

}
