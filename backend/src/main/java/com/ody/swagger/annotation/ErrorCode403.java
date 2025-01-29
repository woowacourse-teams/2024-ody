package com.ody.swagger.annotation;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;
import org.springframework.http.ProblemDetail;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ApiResponse(
        responseCode = "403",
        description = "권한 문제로 요청 거절",
        content = @Content(schema = @Schema(implementation = ProblemDetail.class))
)
public @interface ErrorCode403 {

    @AliasFor(annotation = ApiResponse.class, attribute = "description")
    String description() default "권한 문제로 요청 거절";
}
