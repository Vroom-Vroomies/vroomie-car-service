package com.vroomie.car_service.domain.fleet.drivinglog.dto;

import com.vroomie.car_service.domain.operation.reservation.enums.Purpose;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DrivingLogStartReqDTO {

    @NotNull(message = "운행 목적은 필수입니다.")
    private Purpose purpose;

    @NotBlank(message = "상세 설명은 필수입니다.")
    private String detail;

    @NotNull(message = "시작 주행 거리계(km)는 필수입니다.")
    private Long startOdometer;

    @NotBlank(message = "주행 전 계기판 사진은 필수입니다.")
    private String startOdometerImage;

}
