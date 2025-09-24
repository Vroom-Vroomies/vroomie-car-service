package com.vroomie.car_service.domain.dashboard.projection;

import java.math.BigDecimal;

public interface TcoTrendProjection {
    Integer getYear();
    Integer getMonth();
    BigDecimal getTotalCost();
    BigDecimal getAverageCost();
    Long getVehicleCount();
    BigDecimal getCostPerKm();
}