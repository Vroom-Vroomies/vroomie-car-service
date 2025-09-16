package com.vroomie.car_service.domain.fleet.accident.dto;

import java.time.LocalDate;

public class AccidentListResponseDTO {
    private Long id;
    private Long carId;
    private String empEmail;
    private String note;
    private LocalDate date;
    private boolean save;
}
