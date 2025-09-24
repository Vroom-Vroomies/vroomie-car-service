package com.vroomie.car_service.domain.dashboard.projection;

import java.math.BigDecimal;

public interface EfficiencyTrendProjection {
    String getVehicleId();
    String getVehicleName();
    BigDecimal getInefficiencyScore();
}