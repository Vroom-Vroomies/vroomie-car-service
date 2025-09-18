package com.vroomie.car_service.domain.fleet.drivinglog.dto;

import com.vroomie.car_service.domain.fleet.drivinglog.enums.LogStatus;
import com.vroomie.car_service.domain.fleet.drivinglog.enums.Purpose;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DrivingLogStartResDTO {

    private Long id;
    private String empEmail;
    private Purpose purpose;
    private String detail;
    private Long startOdometer;
    private String startOdometerImage;
    private LocalDateTime startedAt;
    private LogStatus logStatus;
    private LocalDateTime createdAt;

}
