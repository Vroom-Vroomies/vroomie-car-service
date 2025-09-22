package com.vroomie.car_service.domain.contract.car_contract.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PaymentCycle {

    DAILY("일납"),
    WEEKLY("주납"),
    MONTHLY("월납"),
    QUARTERLY("분납"),
    YEARLY("연납");

    public final String paymentCycle;
}
