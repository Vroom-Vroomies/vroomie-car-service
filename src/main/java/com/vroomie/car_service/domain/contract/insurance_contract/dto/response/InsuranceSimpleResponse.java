package com.vroomie.car_service.domain.contract.insurance_contract.dto.response;

import com.vroomie.car_service.domain.contract.insurance_contract.enums.InsuranceStatus;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class InsuranceSimpleResponse {

    private Long insuranceId;
    private Long carId;
    private Date startDate;
    private Date endDate;
    private BigDecimal premium;
    private String insuranceName;
    private InsuranceStatus insuranceStatus;
}
