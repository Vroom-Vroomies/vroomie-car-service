package com.vroomie.car_service.domain.fleet.car.dto.request;

import com.vroomie.car_service.domain.fleet.car.enums.CarStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class CarUpdateRequest {

    private String image;
    private String model;
    private Long totalMileage;
    private String color;
    private CarStatus status;
    private LocalDate insuExpiration;
    private LocalDate lastInspection;
    private Integer inspectionCycle;
}
