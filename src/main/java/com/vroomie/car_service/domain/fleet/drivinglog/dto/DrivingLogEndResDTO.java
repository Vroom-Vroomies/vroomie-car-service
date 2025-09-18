package com.vroomie.car_service.domain.fleet.drivinglog.dto;

import com.vroomie.car_service.domain.fleet.drivinglog.enums.LogStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DrivingLogEndResDTO {

    private Long id;
    private String empEmail;
    private Long endOdometer;
    private String endOdometerImage;
    private LocalDateTime endedAt;
    private LogStatus logStatus;
    private LocalDateTime updatedAt;

}
