package com.vroomie.car_service.domain.contract.car_contract.entity;

import com.vroomie.car_service.domain.contract.car_contract.dto.request.PurchaseUpdateRequest;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
@Entity
@Table(name = "tbl_purchase_detail")
@DiscriminatorValue("PURCHASE")
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
@Getter
@ToString
@SuperBuilder
public class PurchaseContract extends CarContract{

//    @Id
//    private Long id;
    private BigDecimal purchasePrice;
    private BigDecimal downPayment;  // 계약금
    private BigDecimal loanAmount;  // 대출금액
    private BigDecimal interestRate;  // 대출금리

    @Column(name = "loan_term_months")
    private Integer loanTerm;  //  대출 기간(월)

    @Column(name = "monthly_payment")
    private BigDecimal monthlyRepayment;  // 월 상환금

    public void updatePurchaseContract(PurchaseUpdateRequest updateDTO){
        // 1. 공통 필드 업데이트
        super.updateContract(updateDTO);

        this.purchasePrice = updateDTO.getPurchasePrice();
        this.downPayment = updateDTO.getDownPayment();
        this.loanAmount = updateDTO.getLoanAmount();
        this.interestRate = updateDTO.getInterestRate();
        this.loanTerm = updateDTO.getLoanTerm();
        this.monthlyRepayment = updateDTO.getMonthlyRepayment();

        // 3. monthlyFee에 monthlyLease 값 할당
        if(monthlyRepayment != null && monthlyRepayment.compareTo(BigDecimal.ZERO) > 0) {
            super.setMonthlyFee(this.monthlyRepayment);
        }
    }
}
