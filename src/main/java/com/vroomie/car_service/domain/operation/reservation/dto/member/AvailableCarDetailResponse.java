package com.vroomie.car_service.domain.operation.reservation.dto.member;

import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AvailableCarDetailResponse {

    private Long id;
    private String number;
    private String image;
    private String model;
    private String type;
    private String fuelType;
    private String gearType;
    private String usageType;
    private Integer allowableCapacity;
}