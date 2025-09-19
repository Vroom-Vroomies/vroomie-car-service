package com.vroomie.car_service.domain.contract.car_contract.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.vroomie.car_service.domain.contract.car_contract.enums.ContractStatus;
import com.vroomie.car_service.domain.contract.car_contract.enums.ContractType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.sql.Date;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
/*
* Jackson 다형성(@JsonTypeInfo) 활용하기
* RequestBody를 부모 타입으로 받고, JSON에서 contractType 값에 따라 자동으로 자식 DTO로 역직렬화되게 설정 가능
*/
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "contractType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = LeaseRegistRequest.class, name = "LEASE"),
        @JsonSubTypes.Type(value = RentRegistRequest.class, name = "RENT"),
        @JsonSubTypes.Type(value = PurchaseRegistRequest.class, name = "PURCHASE"),
})
public abstract class ContractRegistRequest {

    @NotNull
    private Long carId;

    @NotNull(message = "계약 유형은 필수 항목입니다.")
    private ContractType contractType;

    @NotBlank(message = "공급처명은 필수 항목입니다.")
    private String provider;

    private BigDecimal monthlyFee;

    @Builder.Default
    private boolean isInsured = false;

    @NotNull(message = "계약 시작 일자는 필수 항목입니다.")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "UTC")
    private Date startAt;

    @NotNull(message = "계약 종료 일자는 필수 항목입니다.")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "UTC")
    private Date endAt;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "UTC")
    private Date renewalDate;

    @Builder.Default
    private ContractStatus contractStatus = ContractStatus.NEW;
}
