package com.vroomie.car_service.domain.fleet.accident.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class AccidentDetailDTO {
    private Long id;
    private Long carId;
    private String empName;
    private String type;
    private String note;
    private String detail;
    private LocalDate date;
    private BigDecimal cost;
    private boolean save;
    private List<String> accidentImages;
}
