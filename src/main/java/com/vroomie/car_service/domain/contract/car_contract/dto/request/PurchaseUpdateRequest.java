package com.vroomie.car_service.domain.contract.car_contract.dto.request;

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
// Jackson 역직렬화용 (@NoArgsConstructor)
//Spring에서 @RequestBody로 JSON을 DTO로 받을 때, Jackson은 기본 생성자 + setter로 객체를 만듦
//@SuperBuilder만 있으면 기본 생성자가 없어서 역직렬화 실패 → HttpMessageConversionException 발생
@SuperBuilder  // SuperBuilder는 빌더 생성자만 만들고 기본 생성자를 만들지 않는다.
public class PurchaseUpdateRequest extends ContractUpdateRequest {

    @NotNull(message = "구매 가격은 필수 항목입니다.")
    private BigDecimal purchasePrice;
    private BigDecimal downPayment;

    @Builder.Default
    private BigDecimal loanAmount = BigDecimal.ZERO;
    private BigDecimal interestRate;
    private Integer loanTerm;
    private BigDecimal monthlyRepayment;

}
