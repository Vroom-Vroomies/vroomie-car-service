package com.vroomie.car_service.domain.contract.car_contract.dto.response;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.vroomie.car_service.domain.contract.car_contract.enums.ContractStatus;
import com.vroomie.car_service.domain.contract.car_contract.enums.ContractType;
import com.vroomie.car_service.domain.contract.car_contract.enums.PaymentCycle;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
@Setter
@ToString
@SuperBuilder
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "contractType" // JSON에 이 이름의 프로퍼티로 타입 명시
)
// @JsonTypeInfo와 @JsonSubTypes:
// 이 Jackson 어노테이션을 사용하면, JSON으로 변환될 때 contractType 같은 필드를 JSON에 포함시켜 클라이언트가 어떤 종류의 계약인지 쉽게 알 수 있게 해줍니다.
@JsonSubTypes({
        @JsonSubTypes.Type(value = LeaseContractResponse.class, name = "LEASE"),
        @JsonSubTypes.Type(value = RentContractResponse.class, name = "RENT"),
        @JsonSubTypes.Type(value = PurchaseContractResponse.class, name = "PURCHASE")
})
public class CarContractResponse {

    private Long contractId;
    private Long carId;
    private String carNumber;
    private String carModel;
    private ContractType contractType;
    private String provider;
    private BigDecimal monthlyFee;
    private Boolean isInsured;
    private Date startAt;
    private Date endAt;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Date renewalDate;
    private ContractStatus contractStatus;
    private Integer paymentDay;
    private PaymentCycle paymentCycle;
    private Date firstPaymentDay;
}
