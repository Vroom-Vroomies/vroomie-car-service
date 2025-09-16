package com.vroomie.car_service.domain.contract.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
@Entity
@Table(name = "tbl_purchase_details")
@DiscriminatorValue("PURCHASE")
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
@Getter
@SuperBuilder
public class PurchaseContract extends CarContract{

    private BigDecimal purchasePrice;
    private BigDecimal downPayment;  // 계약금
    private BigDecimal loanAmount;  // 대출금액
    private BigDecimal interestRate;  // 대출금리

    @Column(name = "loan_term_months")
    private Integer loanTerm;  //  대출 기간(월)

    @Column(name = "monthly_payment")
    private BigDecimal monthlyRepayment;  // 월 상환금
}
