package com.vroomie.car_service.domain.contract.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum LeaseType {

    FINANCE("금융리스"),
    OPERATING("운용리스");

    private final String leaseType;
}
