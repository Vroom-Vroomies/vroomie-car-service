package com.vroomie.car_service.domain.fleet.drivinglog.dto.res;

import com.vroomie.car_service.domain.fleet.drivinglog.entity.DrivingLogEntity;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class DrivingLogPdfResDTO {
    private String companyName;
    private String businessNumber;
    private String taxPeriod;
    private String carType;
    private String carNumber;
    private List<DrivingLogInfoDTO> logs;
    private BigDecimal totalDistance;
    private BigDecimal totalBusinessDistance;
    private Double businessRate;
}
