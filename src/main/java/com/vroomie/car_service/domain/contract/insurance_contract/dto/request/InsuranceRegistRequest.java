package com.vroomie.car_service.domain.contract.insurance_contract.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vroomie.car_service.domain.contract.insurance_contract.enums.InsuranceStatus;
import com.vroomie.car_service.domain.contract.insurance_contract.enums.PaymentType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class InsuranceRegistRequest {

    @NotNull
    private Long carId;
    @NotNull(message = "시작 일자를 입력해주세요.")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "UTC")
    private Date startDate;
    @NotNull(message = "종료 일자를 입력해주세요.")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "UTC")
    private Date endDate;
    @NotNull(message = "보험료를 입력해주세요.")
    private BigDecimal premium;
    @NotNull(message = "보험 회사 이름을 입력해주세요.")
    private String companyName;
    @NotNull(message = "보험 이름을 입력해주세요.")
    private String insuranceName;
    @Builder.Default
    private InsuranceStatus insuranceStatus = InsuranceStatus.NEW;
    @Builder.Default
    private PaymentType paymentType = PaymentType.MONTHLY;
    private Integer paymentDay;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "UTC")
    private Date firstPaymentDay;
}
