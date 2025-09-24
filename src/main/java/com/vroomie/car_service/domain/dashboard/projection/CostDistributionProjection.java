package com.vroomie.car_service.domain.dashboard.projection;

import java.math.BigDecimal;

public interface CostDistributionProjection {
    String getCategory();
    BigDecimal getAmount();
    Long getVehicleCount();
}