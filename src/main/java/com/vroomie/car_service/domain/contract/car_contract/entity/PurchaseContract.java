package com.vroomie.car_service.domain.contract.car_contract.entity;

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

//    public PurchaseContract(BigDecimal purchasePrice, BigDecimal downPayment, BigDecimal loanAmount, BigDecimal interestRate, Integer loanTerm, BigDecimal monthlyRepayent){
//        this.purchasePrice = purchasePrice;
//        this.downPayment = downPayment;
//        this.loanAmount = loanAmount;
//        this.interestRate = interestRate;
//        this.loanTerm = loanTerm;
//        this.monthlyRepayment = monthlyRepayent;
//    }
}
