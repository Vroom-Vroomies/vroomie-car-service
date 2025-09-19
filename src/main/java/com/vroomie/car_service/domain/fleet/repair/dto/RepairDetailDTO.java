package com.vroomie.car_service.domain.fleet.repair.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class RepairDetailDTO {
    private Long id;
    private Long carId;
    private String empName;
    private String type;
    private String detail;
    private String status;
    private LocalDate startedAt;
    private LocalDate endedAt;
    private BigDecimal cost;
    private boolean save;
    private List<String> beforeImages;
    private List<String> afterImages;
}