package com.vroomie.car_service.domain.dashboard.projection;

import java.math.BigDecimal;

public interface VehicleEfficiencyProjection {
    String getVehicleId();
    String getVehicleName();
    BigDecimal getInefficiencyScore();
    BigDecimal getTotalCost();
    BigDecimal getTotalDistance();
    BigDecimal getCostPerKm();
    Integer getUsageDays();
    BigDecimal getFuelEfficiency();
    Integer getMaintenanceFrequency();
}