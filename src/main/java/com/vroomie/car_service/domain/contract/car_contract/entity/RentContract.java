package com.vroomie.car_service.domain.contract.car_contract.entity;

import com.vroomie.car_service.domain.contract.car_contract.dto.request.RentUpdateRequest;
import com.vroomie.car_service.domain.contract.car_contract.enums.RentType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
@Entity
@Table(name = "tbl_rent_detail")
@DiscriminatorValue("RENT")
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
@Getter
@ToString
@SuperBuilder
public class RentContract extends CarContract {

//    @Id
//    private Long id;
    private BigDecimal monthlyRent;
    private BigDecimal deposit;  // 보증금

//    @Column(name = "payment_cycle")
//    private Integer paymentCycle;  // 지불주기

    @Column(name = "auto_renewal")
    private Boolean isAutoRenewal;  // 자동 갱신 여부

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private RentType rentType;

    public void updateRentContract(RentUpdateRequest updateDTO){
        // 1. 공통 필드 업데이트
        super.updateContract(updateDTO);

        this.monthlyRent = updateDTO.getMonthlyRent();
        this.deposit = updateDTO.getDeposit();
        this.isAutoRenewal = updateDTO.getIsAutoRenewal();
        this.rentType = updateDTO.getRentType();

        // 3. monthlyFee에 monthlyLease 값 할당
        super.setMonthlyFee(this.monthlyRent);
    }
}
