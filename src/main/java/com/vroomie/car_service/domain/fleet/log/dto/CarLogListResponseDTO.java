package com.vroomie.car_service.domain.fleet.log.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class CarLogListResponseDTO {
    private Long logId;
    private String logType;
    private LocalDate date;
    private String description;
    private String handler; // 처리한 사람
    private String status;
    private BigDecimal cost;

    public CarLogListResponseDTO(Object[] entity){
        this.logId = ((Long) entity[0]).longValue();
        this.logType = (String) entity[1];

        if (entity[2] != null) {
            // object -> timestamp
            Timestamp timestamp = (Timestamp) entity[2];
            // timestamp -> localDateTime ->  localDate
            this.date = timestamp.toLocalDateTime().toLocalDate();
        } else {
            this.date = null;
        }

        this.description = (String) entity[3];
        this.handler = (String) entity[4];
        this.status = (String) entity[5];
        this.cost = (BigDecimal) entity[6];
    }
}
