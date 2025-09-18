package com.vroomie.car_service.domain.fleet.car.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StatusAlertResponse {
    private boolean contract;
    private boolean insurance;
}
