package com.vroomie.car_service.domain.fleet.drivinglog.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import com.vroomie.car_service.domain.operation.reservation.enums.Purpose;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DrivingLogReqDTO {

    private Purpose purpose;
    private String detail;
    private Long startOdometer;
    private String startOdometerImage;
    private Long endOdometer;
    private String endOdometerImage;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

}
