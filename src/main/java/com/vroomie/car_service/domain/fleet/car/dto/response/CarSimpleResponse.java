package com.vroomie.car_service.domain.fleet.car.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vroomie.car_service.domain.fleet.car.enums.CarFuelType;
import com.vroomie.car_service.domain.fleet.car.enums.CarGearType;
import com.vroomie.car_service.domain.fleet.car.enums.CarUsageType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CarSimpleResponse {

    private Long id;
    private String number;
    private String model;
    private String image;
    private CarFuelType fuelType;
    private CarGearType gearType;
    private Integer allowableCapacity;
    private CarUsageType usageType;
    private boolean isRented;
    private boolean isRepairing;
    private StatusAlertResponse statusAlert;

    @JsonProperty("isRented")
    public boolean isRented() {
        return isRented;
    }

    @JsonProperty("isRepairing")
    public boolean isRepairing() {
        return isRepairing;
    }
}