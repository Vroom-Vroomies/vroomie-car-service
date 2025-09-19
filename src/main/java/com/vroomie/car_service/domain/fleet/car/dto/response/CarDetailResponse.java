package com.vroomie.car_service.domain.fleet.car.dto.response;

import com.vroomie.car_service.domain.fleet.car.enums.CarFuelType;
import com.vroomie.car_service.domain.fleet.car.enums.CarGearType;
import com.vroomie.car_service.domain.fleet.car.enums.CarStatus;
import com.vroomie.car_service.domain.fleet.car.enums.CarUsageType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class CarDetailResponse {

    private final Long id;
    private final Long companyId;
    private final String identification;
    private final String number;
    private final String image;
    private final String model;
    private final String type;
    private final Long totalMileage;
    private final String color;
    private final Long year;
    private final CarStatus status;
    private final LocalDate insuExpiration;
    private final LocalDate lastInspection;
    private final Integer inspectionCycle;
    private final Integer allowableCapacity;
    private final CarFuelType fuelType;
    private final CarUsageType usageType;
    private final CarGearType gearType;
}
