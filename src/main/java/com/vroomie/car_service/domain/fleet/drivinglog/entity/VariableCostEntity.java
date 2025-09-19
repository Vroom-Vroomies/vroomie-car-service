package com.vroomie.car_service.domain.fleet.drivinglog.entity;

import com.vroomie.car_service.domain.fleet.drivinglog.enums.Category;
import com.vroomie.car_service.domain.fleet.drivinglog.enums.Payment;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name="tbl_variable_cost")
public class VariableCostEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "log_id")
    private DrivingLogEntity drivingLogEntity;

    @Enumerated(EnumType.STRING)
    private Category category;
    private String reciept;

    @Enumerated(EnumType.STRING)
    private Payment payment;
    private BigDecimal cost;
    private String place;

}
