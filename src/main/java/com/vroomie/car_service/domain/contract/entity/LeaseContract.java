package com.vroomie.car_service.domain.contract.entity;

import com.vroomie.car_service.domain.contract.enums.LeaseType;
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
@Table(name = "tbl_lease_details")
@DiscriminatorValue("LEASE")
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
@Getter
@SuperBuilder
public class LeaseContract extends CarContract{

    private BigDecimal monthlyLease;

    @Column(name = "lease_period_months")
    private Integer leasePeriod;

    private BigDecimal residualValue;   // 잔존가치

    @Column(name = "buyout_option_price")
    private BigDecimal optionPrice;

    private Long mileageLimit;
    private BigDecimal excessMileageRate;

    @Column(name = "type")
    private LeaseType leaseType;
}
