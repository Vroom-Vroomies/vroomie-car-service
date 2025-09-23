package com.vroomie.car_service.domain.contract.car_contract.entity;

import com.vroomie.car_service.domain.contract.car_contract.dto.request.LeaseUpdateRequest;
import com.vroomie.car_service.domain.contract.car_contract.enums.LeaseType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
@Entity
@Table(name = "tbl_lease_detail")
@DiscriminatorValue("LEASE")
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
@Getter
@ToString
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

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private LeaseType leaseType;

    public void updateLeaseContract(LeaseUpdateRequest updateDTO){
        // 1. 공통 필드 업데이트
        super.updateContract(updateDTO);

        // 2. 리스 관련 필드 업데이트
        this.monthlyLease = updateDTO.getMonthlyLease();
        this.leasePeriod = updateDTO.getLeasePeriod();
        this.residualValue = updateDTO.getResidualValue();
        this.optionPrice = updateDTO.getOptionPrice();
        this.mileageLimit = updateDTO.getMileageLimit();
        this.excessMileageRate = updateDTO.getExcessMileageRate();
        this.leaseType = updateDTO.getLeaseType();

        // 3. monthlyFee에 monthlyLease 값 할당
        super.setMonthlyFee(this.monthlyLease);
    }
}
