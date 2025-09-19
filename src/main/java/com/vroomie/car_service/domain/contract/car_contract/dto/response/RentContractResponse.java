package com.vroomie.car_service.domain.contract.car_contract.dto.response;

import com.vroomie.car_service.domain.contract.car_contract.enums.RentType;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
@Setter
@ToString
@SuperBuilder
public class RentContractResponse extends CarContractResponse {

    private BigDecimal monthlyRent;
    private BigDecimal deposit;
//    private Integer paymentCycle;
    private Boolean isAutoRenewal;
    private RentType rentType;
    private BigDecimal annualCost;   // monthlyRent * 12
    private BigDecimal totalCost;    // monthlyRent * 계약 기간
    private BigDecimal depositRecoveryCost;   // deposit

    public void calculateAmounts() {
        if(monthlyRent != null){
            this.annualCost = monthlyRent.multiply(new BigDecimal(12));

            if(super.getStartAt() != null && super.getEndAt() != null){
                LocalDate startDate = super.getStartAt().toLocalDate();
                LocalDate endDate = super.getEndAt().toLocalDate();
                long months = ChronoUnit.MONTHS.between(startDate, endDate);

                // 장기 렌트 시 하루라도 넘으면 다음 달까지 계산
                if(rentType != null && rentType == RentType.LONG_RENT){
                    long days = ChronoUnit.DAYS.between(startDate, endDate);

                    if(days >= 1){
                        this.totalCost = monthlyRent.multiply(BigDecimal.valueOf(months + 1));
                    }else {
                        this.totalCost = monthlyRent.multiply(BigDecimal.valueOf(months));
                    }
                // 단기 렌트 시 일자 계산
                }else {
                    LocalDate plusDays = startDate.plusMonths(months);
                    long days = ChronoUnit.DAYS.between(plusDays, endDate);

                    BigDecimal dailyRate = monthlyRent.divide(BigDecimal.valueOf(30), RoundingMode.HALF_UP);
                    this.totalCost = monthlyRent.multiply(BigDecimal.valueOf(months)).add(dailyRate.multiply(BigDecimal.valueOf(days)));
                }
            }
        }

        if(deposit != null){
            this.depositRecoveryCost = deposit;
        }


    }
}
