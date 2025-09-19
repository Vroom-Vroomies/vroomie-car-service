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
public class DrivingLogSummaryResDTO {
    private Long id;
    private String empEmail;
    private Long carId;
    private LogStatus logStatus;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private LocalDateTime updatedAt;
}
