package com.vroomie.car_service.domain.fleet.drivinglog.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class GpsDistanceCalculator {

    private static final double EARTH_RADIUS_KM = 6371.0; // 지구 반지름 (km)

    /**
     * 두 좌표 간 GPS 거리 계산 (km)
     *
     * @param startLat 시작 위도
     * @param startLng 시작 경도
     * @param endLat   종료 위도
     * @param endLng   종료 경도
     * @return BigDecimal 거리(km)
     */
    public static BigDecimal calculateDistance(BigDecimal startLat, BigDecimal startLng,
                                               BigDecimal endLat, BigDecimal endLng) {
        if (startLat == null || startLng == null || endLat == null || endLng == null) {
            return BigDecimal.ZERO;
        }

        double lat1 = startLat.doubleValue();
        double lon1 = startLng.doubleValue();
        double lat2 = endLat.doubleValue();
        double lon2 = endLng.doubleValue();

        // 위도/경도 차이
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        // Haversine 공식
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distanceKm = EARTH_RADIUS_KM * c;

        return BigDecimal.valueOf(distanceKm);
    }
}
