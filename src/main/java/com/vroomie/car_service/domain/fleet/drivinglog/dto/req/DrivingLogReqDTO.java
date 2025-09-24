package com.vroomie.car_service.domain.fleet.drivinglog.dto.req;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DrivingLogReqDTO {

    private Long startOdometer;
    private String startOdometerImage;
    private Long endOdometer;
    private String endOdometerImage;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private String note;

}
