package com.vroomie.car_service.domain.contract.entity;

import com.vroomie.car_service.domain.contract.enums.RentType;
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
@Table(name = "tbl_rent_details")
@DiscriminatorValue("RENT")
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
@Getter
@SuperBuilder
public class RentContract extends CarContract {

    private BigDecimal monthlyRent;
    private BigDecimal deposit;  // 보증금
    private Integer paymentCycle;  // 지불주기
    private Boolean isAutoRenewal;  // 자동 갱신 여부

    @Column(name = "type")
    private RentType rentType;
}
