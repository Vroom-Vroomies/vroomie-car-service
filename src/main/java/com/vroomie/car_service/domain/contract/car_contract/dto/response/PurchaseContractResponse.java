package com.vroomie.car_service.domain.contract.car_contract.dto.response;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
@Setter
@ToString
@SuperBuilder
public class PurchaseContractResponse extends CarContractResponse{

    private BigDecimal purchasePrice;
    private BigDecimal downPayment;
    private BigDecimal loanAmount;
    private BigDecimal interestRate;
    private Integer loanTerm;
    private BigDecimal monthlyRepayment;  //
    private BigDecimal totalRepayment;   // 총 상환금액 = monthlyRepayment * loanTerm
    private BigDecimal totalPurchaseCost;  // totalRepayment + downPayment
    private BigDecimal intestCost;  //  totalRepayment - loanAmount

    public void calculateAmounts() {
        if(monthlyRepayment != null && loanTerm != null){
            this.totalRepayment = monthlyRepayment.multiply(new BigDecimal(loanTerm));
        }

        if(totalRepayment != null && downPayment != null){
            this.totalPurchaseCost = totalRepayment.add(downPayment);
            this.intestCost = totalPurchaseCost;
        }
    }
}
