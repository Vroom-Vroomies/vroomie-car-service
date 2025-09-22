package com.vroomie.car_service.domain.contract.actual_cost.entity;

import com.vroomie.car_service.domain.contract.car_contract.entity.CarContract;
import com.vroomie.car_service.domain.contract.insurance_contract.entity.InsuContractEntity;
import com.vroomie.car_service.domain.fleet.car.entity.CarEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

@Slf4j
@Entity
@Table(name = "tbl_contract_cost")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class ActualCost {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "car_id", referencedColumnName = "id")
    private CarEntity car;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "contract_id", referencedColumnName = "id")
    private CarContract contract;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "insurance_id", referencedColumnName = "id")
    private InsuContractEntity insurance;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cost_type_id", referencedColumnName = "id")
    private ContractCostType costType;

    private BigDecimal amount;

    private Date costDate;

    private String description;

    private Timestamp createdAt;

    private Timestamp updatedAt;
}
