package com.ody.swagger.annotation;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Parameter(
        name = "Authorization",
        description = "디바이스 토큰 인증 헤더",
        required = true,
        in = ParameterIn.HEADER,
        schema = @Schema(type = "string"),
        example = "device-token="
)
public @interface DeviceTokenHeader {

}
