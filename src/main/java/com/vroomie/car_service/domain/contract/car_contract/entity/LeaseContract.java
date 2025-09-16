package com.vroomie.car_service.domain.contract.car_contract.entity;

import com.vroomie.car_service.domain.contract.car_contract.enums.LeaseType;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
@Entity
@Table(name = "tbl_lease_detail")
@DiscriminatorValue("LEASE")
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
@Getter
public class LeaseContract extends CarContract{

    @Id
    private Long id;

    private BigDecimal monthlyLease;

    @Column(name = "lease_period_months")
    private Integer leasePeriod;

    private BigDecimal residualValue;   // 잔존가치

    @Column(name = "buyout_option_price")
    private BigDecimal optionPrice;

    private Long mileageLimit;
    private BigDecimal excessMileageRate;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private LeaseType leaseType;
}
