package com.ody.route.dto;

import java.util.Optional;
import java.util.OptionalLong;

public record OdsayResponse(

        Optional<String> code,
        Optional<String> message,
        OptionalLong minutes
) {

}
