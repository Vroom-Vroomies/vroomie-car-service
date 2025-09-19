package com.vroomie.car_service.domain.contract.insurance_contract.entity;

import com.vroomie.car_service.domain.contract.insurance_contract.dto.request.InsuranceRegistRequest;
import com.vroomie.car_service.domain.contract.insurance_contract.enums.InsuranceStatus;
import com.vroomie.car_service.domain.contract.insurance_contract.enums.PaymentType;
import com.vroomie.car_service.domain.fleet.car.entity.CarEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.beans.ConstructorProperties;
import java.math.BigDecimal;
import java.sql.Date;

@Slf4j
@Entity
@Table(name = "tbl_insurance")
@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
@Builder
public class InsuContractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "car_id")
    private CarEntity car;

    @Column(name = "start_date")
    private Date startDate;
    @Column(name = "end_date")
    private Date endDate;
    private BigDecimal premium;
    @Column(name = "company")
    private String companyName;
    @Column(name = "name")
    private String insuranceName;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private InsuranceStatus insuranceStatus;
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type")
    private PaymentType paymentType;
    @Column(name = "payment_day")
    private Integer paymentDay;
    @Column(name = "first_payment_day")
    private Date firstPaymentDay;

    public void updateInsuranceInfo(InsuranceRegistRequest updateDTO){
        this.startDate = updateDTO.getStartDate();
        this.endDate = updateDTO.getEndDate();
        this.premium = updateDTO.getPremium();
        this.insuranceName = updateDTO.getInsuranceName();
        this.companyName = updateDTO.getCompanyName();
        this.insuranceStatus = updateDTO.getInsuranceStatus();
        this.paymentType = updateDTO.getPaymentType();
        this.paymentDay = updateDTO.getPaymentDay();
        this.firstPaymentDay = updateDTO.getFirstPaymentDay();

    }

}
