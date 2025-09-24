package com.vroomie.car_service.domain.fleet.log.projection;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface CarLogProjection {
    Long getLogId();
    String getLogType();
    LocalDate getDate();
    String getDescription();
    String getHandler();
    String getStatus();
    BigDecimal getCost();
}