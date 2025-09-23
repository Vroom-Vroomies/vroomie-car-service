package com.vroomie.car_service.domain.contract.insurance_contract.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PaymentType {

    MONTHLY("월납입");

    private final String paymentType;
}
