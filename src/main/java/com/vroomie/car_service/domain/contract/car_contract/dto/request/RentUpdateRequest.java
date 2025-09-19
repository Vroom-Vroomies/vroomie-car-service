package com.vroomie.car_service.domain.contract.car_contract.dto.request;

import com.vroomie.car_service.domain.contract.car_contract.enums.RentType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class RentUpdateRequest extends ContractUpdateRequest{

    @NotNull(message = "월 렌트비는 필수 항목입니다.")
    private BigDecimal monthlyRent;
    private BigDecimal deposit;

//    @NotNull(message = "지불 주기는 필수 항목입니다.")
//    private Integer paymentCycle;

    @Builder.Default
    private Boolean isAutoRenewal = false;

    private RentType rentType;
}
