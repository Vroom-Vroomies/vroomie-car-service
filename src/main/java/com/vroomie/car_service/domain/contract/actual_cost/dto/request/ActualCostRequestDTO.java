package com.vroomie.car_service.domain.contract.actual_cost.dto.request;

import com.vroomie.car_service.domain.contract.actual_cost.entity.ContractCostType;
import com.vroomie.car_service.domain.contract.car_contract.enums.ContractStatus;
import com.vroomie.car_service.domain.contract.car_contract.enums.ContractType;
import com.vroomie.car_service.domain.fleet.car.entity.CarEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ActualCostRequestDTO {

    private Long id;
    private Long carId;
    private Long contractId;
    private Long insuranceId;
    private Long costTypeId;
    private BigDecimal amount;
    private Date costDate;
    private String description;
    private Timestamp createdAt;
    private Timestamp updatedAt;

}
