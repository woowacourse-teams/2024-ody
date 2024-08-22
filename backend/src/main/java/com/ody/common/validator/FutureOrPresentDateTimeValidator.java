package com.ody.common.validator;

import com.ody.common.annotation.FutureOrPresentDateTime;
import com.ody.common.exception.OdyServerErrorException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class FutureOrPresentDateTimeValidator implements ConstraintValidator<FutureOrPresentDateTime, Object> {

    private String dateFieldName;
    private String timeFieldName;

    @Override
    public void initialize(FutureOrPresentDateTime constraintAnnotation) {
        dateFieldName = constraintAnnotation.dateFieldName();
        timeFieldName = constraintAnnotation.timeFieldName();
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext constraintValidatorContext) {
        try {
            Class<?> objectClass = object.getClass();
            Method dateGetter = objectClass.getMethod(dateFieldName);
            Method timeGetter = objectClass.getMethod(timeFieldName);

            LocalDate dateInput = (LocalDate) dateGetter.invoke(object);
            LocalTime timeInput = (LocalTime) timeGetter.invoke(object);

            LocalDateTime dateTimeInput = LocalDateTime.of(dateInput, timeInput);
            LocalDateTime now = LocalDateTime.now();

            if (dateInput.isEqual(now.toLocalDate())) {
                return dateTimeInput.isAfter(now);
            }
            return dateInput.isAfter(LocalDate.now());
        } catch (Exception exception) {
            throw new OdyServerErrorException(exception.getMessage());
        }
    }
}
