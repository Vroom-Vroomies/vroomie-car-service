package com.vroomie.car_service.domain.contract.car_contract.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PaymentCycle {

    MONTHLY("월납");

    public final String paymentCycle;
}
