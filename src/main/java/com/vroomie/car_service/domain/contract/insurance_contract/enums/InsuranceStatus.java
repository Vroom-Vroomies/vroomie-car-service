package com.vroomie.car_service.domain.contract.insurance_contract.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum InsuranceStatus {

    NEW("초기"),
    RENEWED("갱신"),
    EXPIRED("만료");

    private final String insuranceStatus;
}
