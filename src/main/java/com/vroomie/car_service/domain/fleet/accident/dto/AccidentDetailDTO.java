package com.vroomie.car_service.domain.fleet.accident.dto;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
public class AccidentDetailDTO {
    private Long id;
    private Long carId;
    private String empEmail;
    private String type;
    private String note;
    private String detail;
    private LocalDate date;
    private BigDecimal cost;
    private boolean save;
}
