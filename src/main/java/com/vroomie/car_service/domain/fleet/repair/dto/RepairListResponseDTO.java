package com.vroomie.car_service.domain.fleet.repair.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class RepairListResponseDTO {
    private Long id;
    private Long carId;
    private String empName;
    private String type;
    private String status;
    private LocalDate startedAt;
    private LocalDate endedAt;
    private boolean save;
}