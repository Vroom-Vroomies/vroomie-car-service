package com.vroomie.car_service.domain.fleet.drivinglog.dto.res;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class DrivingLogInfoDTO {
    private String date;
    private String department;
    private String name;
    private Long startOdometer;
    private Long endOdometer;
    private BigDecimal distance;
    private BigDecimal commuteDistance;
    private BigDecimal businessDistance;
    private String note;
}
