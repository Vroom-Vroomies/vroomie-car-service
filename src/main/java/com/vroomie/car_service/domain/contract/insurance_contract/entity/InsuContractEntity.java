package com.vroomie.car_service.domain.contract.insurance_contract.entity;

import com.vroomie.car_service.domain.fleet.car.entity.CarEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "tbl_insurance")
public class InsuContractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id")
    private CarEntity car;
}
