package com.vroomie.car_service.domain.fleet.drivinglog.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DrivingLogEndReqDTO {

    @NotNull(message = "종료 주행 거리계(km)는 필수입니다.")
    private Long endOdometer;

    @NotNull(message = "주행 후 계기판 사진은 필수입니다.")
    private String endOdometerImage;

}
