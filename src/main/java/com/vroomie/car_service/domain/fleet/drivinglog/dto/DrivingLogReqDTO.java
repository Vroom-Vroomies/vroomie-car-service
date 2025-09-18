package com.vroomie.car_service.domain.fleet.drivinglog.dto;

import com.vroomie.car_service.domain.fleet.drivinglog.enums.Purpose;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
