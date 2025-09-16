package com.vroomie.car_service.domain.contract.car_contract.dto.response;

import com.vroomie.car_service.domain.contract.car_contract.enums.LeaseType;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
@Setter
@ToString
public class LeaseContractResponse extends CarContractResponse {

    private BigDecimal monthlyLease;
    private Integer leasePeriod;
    private BigDecimal residualValue;
    private BigDecimal optionPrice;
    private Long mileageLimit;
    private BigDecimal excessMileageRate;
    private LeaseType leaseType;
    private BigDecimal totalLeaseFee;  // monthlyLease * leasePeriod
    private BigDecimal totalAmountPurchase;  // totalLeaseFee + optionPrice
    private BigDecimal onlyLeaseFee;  // totalLeaseFee

    public void calculateAmounts() {
        if(monthlyLease != null && leasePeriod != null) {
            this.totalLeaseFee = monthlyLease.multiply(new BigDecimal(leasePeriod));
            this.onlyLeaseFee = this.totalLeaseFee;
        }
        if(optionPrice != null && totalLeaseFee != null) {
            this.totalAmountPurchase = totalLeaseFee.add(optionPrice);
        }
    }
}
