package com.vroomie.car_service.domain.contract.car_contract.dto.response;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

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
    private BigDecimal interestCost;  //  totalRepayment - loanAmount


    public void calculateAmounts() {
        // 월 상환금과 대출기간으로 총 상환액 계산
        if (monthlyRepayment != null && loanTerm != null && loanTerm > 0) {
            this.totalRepayment = monthlyRepayment.multiply(BigDecimal.valueOf(loanTerm));
        } else {
            this.totalRepayment = BigDecimal.ZERO;
        }

        // 대출이 있는 경우
        if (loanAmount != null && loanAmount.compareTo(BigDecimal.ZERO) > 0) {
            this.totalPurchaseCost = (totalRepayment != null ? totalRepayment : BigDecimal.ZERO)
                    .add(downPayment != null ? downPayment : BigDecimal.ZERO);

            this.interestCost = (totalRepayment != null ? totalRepayment : BigDecimal.ZERO)
                    .subtract(loanAmount);
        }
        // 대출이 없는 경우 → 현금 구매
        else {
            this.totalPurchaseCost = purchasePrice != null ? purchasePrice : BigDecimal.ZERO;
            this.interestCost = BigDecimal.ZERO;
        }
        super.setMonthlyFee(monthlyRepayment);
    }

}
