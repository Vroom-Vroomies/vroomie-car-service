package com.vroomie.car_service.domain.contract.insurance_contract.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PaymentType {

    LUMP_SUM("일시납입"),
    MONTHLY("월납입"),
    QUARTERLY("분기납입");

    private final String paymentType;
}
