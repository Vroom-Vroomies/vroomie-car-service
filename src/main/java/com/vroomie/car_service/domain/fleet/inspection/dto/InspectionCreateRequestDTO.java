package com.vroomie.car_service.domain.fleet.inspection.dto;

import com.vroomie.car_service.domain.fleet.inspection.enums.InspectionType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class InspectionCreateRequestDTO {
    private LocalDateTime date;
    private String centerName;
    private String centerLocation;
    private String inspectorName;
    private InspectionType inspectionType;
    private String finalResult;
    private LocalDate validUntil;
    private String failureReason;
    private String result;
    private String remarks;
    private String creatorEmail;
}
