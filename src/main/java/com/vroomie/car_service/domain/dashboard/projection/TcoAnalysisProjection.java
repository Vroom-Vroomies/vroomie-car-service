package com.vroomie.car_service.domain.dashboard.projection;

import java.math.BigDecimal;

public interface TcoAnalysisProjection {
    String getVehicleId();
    String getVehicleName();
    String getVehicleType();
    String getContractType();
    BigDecimal getTotalCost();
    BigDecimal getContractCost();
    BigDecimal getOperationalCost();
    BigDecimal getMaintenanceCost();
    BigDecimal getMileage();
    Integer getUsageDays();
}