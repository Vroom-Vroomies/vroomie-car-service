package com.vroomie.car_service.domain.contract.car_contract.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseRegistRequest extends ContractRegistRequest {

    @NotNull(message = "구매 가격은 필수 항목입니다.")
    private BigDecimal purchasePrice;
    private BigDecimal downPayment;

    @Builder.Default
    private BigDecimal loanAmount = BigDecimal.ZERO;
    private BigDecimal interestRate;
    private Integer loanTerm;
    private BigDecimal monthlyRepayment;
}
