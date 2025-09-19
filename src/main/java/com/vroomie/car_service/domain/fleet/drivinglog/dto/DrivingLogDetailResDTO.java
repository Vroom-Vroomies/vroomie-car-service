package com.vroomie.car_service.domain.fleet.drivinglog.dto;

import com.vroomie.car_service.domain.fleet.drivinglog.enums.LogStatus;
import com.vroomie.car_service.domain.operation.reservation.enums.Purpose;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DrivingLogDetailResDTO {

    private Long id;
    private String empEmail;
    private Long carId;
    private Purpose purpose;
    private String detail;
    private Long startOdometer;
    private String startOdometerImage;
    private LogStatus logStatus;
    private Long endOdometer;
    private String endOdometerImage;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
