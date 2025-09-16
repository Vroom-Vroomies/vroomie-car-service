package com.vroomie.car_service.domain.contract.car_contract.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum RentType {

    SHORT_RENT("단기렌탈"),
    LONG_RENT("장기렌탈");

    private final String rentType;
}
