package com.ody.common.validator;

import com.ody.common.annotation.SupportRegion;
import com.ody.common.exception.OdyServerErrorException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.Method;
import java.math.BigDecimal;

public class SupportRegionValidator implements ConstraintValidator<SupportRegion, Object> {

    private static final BigDecimal MIN_LATITUDE = new BigDecimal("36.88");
    private static final BigDecimal MAX_LATITUDE = new BigDecimal("38.3");
    private static final BigDecimal MIN_LONGITUDE = new BigDecimal("125.6");
    private static final BigDecimal MAX_LONGITUDE = new BigDecimal("127.9");
    private static final BigDecimal MISSING_COORDINATES = new BigDecimal("0.0");

    private String latitudeFieldName;
    private String longitudeFieldName;

    @Override
    public void initialize(SupportRegion constraintAnnotation) {
        latitudeFieldName = constraintAnnotation.latitudeFieldName();
        longitudeFieldName = constraintAnnotation.longitudeFieldName();
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext constraintValidatorContext) {
        try {
            Class<?> objectClass = object.getClass();
            Method latitudeGetter = objectClass.getMethod(latitudeFieldName);
            Method longitudeGetter = objectClass.getMethod(longitudeFieldName);

            String latitude = (String) latitudeGetter.invoke(object);
            String longitude = (String) longitudeGetter.invoke(object);

            return isInLatitudeRange(latitude) && isInLongitudeRange(longitude);
        } catch (Exception exception) {
            throw new OdyServerErrorException(exception.getMessage());
        }
    }

    private boolean isInLatitudeRange(String latitude) {
        BigDecimal latitudeValue = new BigDecimal(latitude);
        return isMissingCoordinate(latitudeValue)
                || (MIN_LATITUDE.compareTo(latitudeValue) <= 0 && MAX_LATITUDE.compareTo(latitudeValue) >= 0);
    }

    private boolean isInLongitudeRange(String longitude) {
        BigDecimal longitudeValue = new BigDecimal(longitude);
        return isMissingCoordinate(longitudeValue)
                || (MIN_LONGITUDE.compareTo(longitudeValue) <= 0 && MAX_LONGITUDE.compareTo(longitudeValue) >= 0);
    }

    private boolean isMissingCoordinate(BigDecimal coordinate) {
        return MISSING_COORDINATES.compareTo(coordinate) == 0;
    }
}
