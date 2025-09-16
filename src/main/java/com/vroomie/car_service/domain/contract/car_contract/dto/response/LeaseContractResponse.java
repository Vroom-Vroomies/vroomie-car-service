package com.vroomie.car_service.domain.contract.car_contract.dto.response;

import com.vroomie.car_service.domain.contract.car_contract.enums.LeaseType;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.math.BigInteger;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
@Setter
@ToString
@SuperBuilder
public class LeaseContractResponse extends CarContractResponse{

    private BigDecimal monthlyLease;
    private Integer leasePeriod;
    private BigDecimal residualValue;
    private BigDecimal optionPrice;
    private Long mileageLimit;
    private BigDecimal excessMileageRate;
    private LeaseType leaseType;
//    private BigDecimal totalLeaseFee;
//    private BigDecimal totalAmountPurchase;
//    private BigDecimal onlyLeaseFee;
}
