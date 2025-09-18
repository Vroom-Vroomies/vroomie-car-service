package com.vroomie.car_service.domain.fleet.repair.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class RepairCreateRequestDTO {
    private Long carId;
    private String empEmail;
    private String type;
    private String detail;
    private String status;
    private LocalDate startedAt;
    private LocalDate endedAt;
    private BigDecimal cost;
}