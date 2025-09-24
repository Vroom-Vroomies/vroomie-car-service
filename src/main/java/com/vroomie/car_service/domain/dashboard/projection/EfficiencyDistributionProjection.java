package com.vroomie.car_service.domain.dashboard.projection;

import java.math.BigDecimal;

public interface EfficiencyDistributionProjection {
    String getEfficiencyGrade();
    Long getVehicleCount();
    Double getPercentage();
    BigDecimal getAvgInefficiencyScore();
    BigDecimal getAvgCostPerKm();
}