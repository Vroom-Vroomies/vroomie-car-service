package com.vroomie.car_service.domain.contract.insurance_contract.dto.response;

import com.vroomie.car_service.domain.contract.insurance_contract.enums.InsuranceStatus;
import com.vroomie.car_service.domain.contract.insurance_contract.enums.PaymentType;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class InsuranceDetailResponse {

    private Long insuranceId;
    private Long carId;
    private String carNumber;
    private Date startDate;
    private Date endDate;
    private BigDecimal premium;
    private String companyName;
    private String insuranceName;
    private InsuranceStatus insuranceStatus;
    private PaymentType paymentType;
    private Integer paymentDay;
    private Date firstPaymentDay;
}
