package com.ody.common.annotation;

import com.ody.common.validator.SupportRegionValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = SupportRegionValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SupportRegion {

    String message() default "수도권 내 위경도 좌표만 가능합니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String latitudeFieldName();

    String longitudeFieldName();
}
