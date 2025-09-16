package com.vroomie.car_service.domain.contract.car_contract.dto.response;

import com.vroomie.car_service.domain.contract.car_contract.enums.RentType;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
@Setter
@ToString
@SuperBuilder
public class RentContractResponse extends CarContractResponse {

    private BigDecimal monthlyRent;
    private BigDecimal deposit;
    private Integer paymentCycle;
    private Boolean isAutoRenewal;
    private RentType rentType;
//    private BigDecimal annualCost;
//    private BigDecimal totalCost;
//    private BigDecimal depositRecoveryCost;
}
