package com.ody.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DistanceCalculator {

    private static final double HAVERSINE_CONSTANT = 60 * 1.1515 * 1609.344; // 거리 계산을 위한 하버사인 공식 상수
    private static final double RADIAN_PI = 180.0;

    public static double calculate(
            double originLatitude,
            double originLongitude,
            double targetLatitude,
            double targetLongitude
    ) {
        double theta = originLongitude - targetLongitude;
        double degree = calculateDegree(originLatitude, targetLatitude, theta);

        return convertRadianToDegree(Math.acos(degree)) * HAVERSINE_CONSTANT;
    }

    private static double calculateDegree(double originLatitude, double targetLatitude, double theta) {
        return Math.sin(convertDegreeToRadian(originLatitude))
                * Math.sin(convertDegreeToRadian(targetLatitude))
                + Math.cos(convertDegreeToRadian(originLatitude))
                * Math.cos(convertDegreeToRadian(targetLatitude))
                * Math.cos(convertDegreeToRadian(theta));
    }

    private static double convertDegreeToRadian(double degree) {
        return (degree * Math.PI / RADIAN_PI);
    }

    private static double convertRadianToDegree(double radian) {
        return (radian * RADIAN_PI / Math.PI);
    }
}
