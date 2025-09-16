package com.vroomie.car_service.domain.contract.car_contract.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ContractType {

    LEASE("리스"),
    RENT("렌트"),
    PURCHASE("구매");

    private final String contractType;
}
