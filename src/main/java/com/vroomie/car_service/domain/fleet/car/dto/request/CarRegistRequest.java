// car/dto/request/CarCreateRequest.java

package com.vroomie.car_service.domain.fleet.car.dto.request;

import com.vroomie.car_service.domain.fleet.car.enums.CarFuelType;
import com.vroomie.car_service.domain.fleet.car.enums.CarGearType;
import com.vroomie.car_service.domain.fleet.car.enums.CarStatus;
import com.vroomie.car_service.domain.fleet.car.enums.CarUsageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class CarRegistRequest {

    @NotNull(message = "회사 ID는 필수입니다.")
    private Long companyId;

    @NotBlank(message = "차대번호는 필수입니다.")
    private String identification;

    @NotBlank(message = "차량번호는 필수입니다.")
    private String number;

    private String image;

    @NotBlank(message = "모델명은 필수입니다.")
    private String model;

    @NotBlank(message = "차종은 필수입니다.")
    private String type;

//    @NotNull(message = "주행거리는 필수입니다.")
    private Long totalMileage;

    @NotBlank(message = "색상은 필수입니다.")
    private String color;

    @NotNull(message = "연식은 필수입니다.")
    private Integer year;

    @NotNull(message = "상태는 필수입니다.")
    private CarStatus status;

    private LocalDate insuExpiration;
    private LocalDate lastInspection;
    private Integer inspectionCycle;
    private Integer allowableCapacity;
    private CarFuelType fuelType;
    private CarUsageType usageType;
    private CarGearType gearType;
}