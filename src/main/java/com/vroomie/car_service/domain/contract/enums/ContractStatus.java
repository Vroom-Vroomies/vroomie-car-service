package com.vroomie.car_service.domain.contract.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.service.annotation.GetExchange;

@RequiredArgsConstructor
@Getter
public enum ContractStatus {

    NEW("초기"),
    RENEWED("갱신"),
    EXPIRED("만료");

    private final String contractStatus;
}
