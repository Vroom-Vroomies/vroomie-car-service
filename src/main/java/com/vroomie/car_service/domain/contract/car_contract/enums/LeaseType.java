package com.vroomie.car_service.domain.contract.car_contract.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum LeaseType {

    FINANCE_LEASE("금융리스"),
    OPERATING_LEASE("운용리스");

    private final String leaseType;
}
