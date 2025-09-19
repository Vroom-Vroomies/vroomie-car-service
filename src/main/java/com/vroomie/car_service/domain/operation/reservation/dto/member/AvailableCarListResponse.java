package com.vroomie.car_service.domain.operation.reservation.dto.member;

import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AvailableCarListResponse {

    private Long id;
    private String number;
    private String image;
    private String model;
    private String fuelType;
    private String gearType;
    private String usageType;
    private Integer allowableCapacity;
}