package com.vroomie.car_service.domain.fleet.accident.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class AccidentListResponseDTO {
    private Long id;
    private Long carId;
    private String empName;
    private String note;
    private LocalDate date;
    private boolean save;
}
