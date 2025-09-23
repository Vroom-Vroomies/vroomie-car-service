package com.vroomie.car_service.domain.fleet.car.dto.request;

import com.vroomie.car_service.domain.fleet.car.enums.CarFuelType;
import com.vroomie.car_service.domain.fleet.car.enums.CarGearType;
import com.vroomie.car_service.domain.fleet.car.enums.CarStatus;
import com.vroomie.car_service.domain.fleet.car.enums.CarUsageType;
import com.vroomie.car_service.global.util.Trimmed;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class CarUpdateRequest {

    @Trimmed
    @Pattern(regexp = "^[A-Z]{5}\\d{2}[A-Z]{4}\\d{6}$", message = "차대번호는 영문 대문자 5자리, 숫자 2자리, 영문 대문자 4자리, 숫자 6자리의 17자리 형식이어야 합니다.")
    private String identification;

    @Trimmed
    private String number;

    private String image;

    @Trimmed
    private String model;

    @Trimmed
    private String type;

    @Min(value = 0, message = "주행 거리는 0 이상이어야 합니다.")
    private Long totalMileage;

    @Trimmed
    private String color;

    @Min(value = 1900, message = "연식은 1900 이상이어야 합니다.")
    private Integer year;

    private CarStatus status;

    private LocalDate insuExpiration;
    private LocalDate lastInspection;

    @Min(value = 0, message = "검사 주기는 0 이상이어야 합니다.")
    private Integer inspectionCycle;

    @Min(value = 1, message = "수용 인원은 1 이상이어야 합니다.")
    private Integer allowableCapacity;

    private CarFuelType fuelType;
    private CarUsageType usageType;
    private CarGearType gearType;
}