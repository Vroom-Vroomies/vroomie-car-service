package com.vroomie.car_service.domain.fleet.drivinglog.dto.res;

import com.vroomie.car_service.domain.fleet.drivinglog.enums.LogStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DrivingLogEndResDTO {

    private Long id;
    private String empEmail;
    private Long startOdometer;
    private String startOdometerImage;
    private Long endOdometer;
    private String endOdometerImage;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private BigDecimal startLat;
    private BigDecimal startLng;
    private BigDecimal endLat;
    private BigDecimal endLng;
    private String startLocation;
    private String endLocation;
    private BigDecimal gpsDistance;
    private BigDecimal odometerDistance;
    private LogStatus logStatus;
    private LocalDateTime updatedAt;

}
