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
public class DrivingLogStartResDTO {

    private Long id;
    private String empEmail;
    private Long startOdometer;
    private String startOdometerImage;
    private LocalDateTime startedAt;
    private BigDecimal startLat;
    private BigDecimal startLng;
    private String startLocation;
    private LogStatus logStatus;
    private LocalDateTime createdAt;

}
