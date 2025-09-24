package com.vroomie.car_service.domain.fleet.log.dto;

import com.vroomie.car_service.domain.fleet.log.projection.CarLogProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CarLogListResponseDTO {
    private Long logId;
    private String logType;
    private LocalDate date;
    private String description;
    private String handler;
    private String status;
    private BigDecimal cost;

    // Projection -> DTO
    public CarLogListResponseDTO(CarLogProjection projection) {
        this.logId = projection.getLogId();
        this.logType = projection.getLogType();
        this.date = projection.getDate();
        this.description = projection.getDescription();
        this.handler = projection.getHandler();
        this.status = projection.getStatus();
        this.cost = projection.getCost();
    }
}