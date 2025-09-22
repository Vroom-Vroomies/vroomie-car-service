package com.vroomie.car_service.domain.contract.car_contract.dto.request;

import com.vroomie.car_service.domain.contract.car_contract.enums.LeaseType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LeaseRegistRequest extends ContractRegistRequest{

    @NotNull(message = "월 리스료는 필수 항목입니다.")
    private BigDecimal monthlyLease;

    @NotNull(message = "리스 기간은 필수 항목입니다.")
    private Integer leasePeriod;

    private BigDecimal residualValue;
    private BigDecimal optionPrice;
    private Long mileageLimit;
    private BigDecimal excessMileageRate;
    private LeaseType leaseType;

    public void calculateAmounts() {
        // 주행거리 초과
        super.monthlyFee = this.monthlyLease;
    }
}
