package com.vroomie.car_service.domain.contract.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum RentType {

    SHORT("단기렌탈"),
    LONG("장기렌탈");

    private final String rentType;
}
