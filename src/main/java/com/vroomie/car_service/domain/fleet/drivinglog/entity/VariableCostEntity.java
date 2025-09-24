package com.vroomie.car_service.domain.fleet.drivinglog.entity;

import com.vroomie.car_service.domain.fleet.drivinglog.enums.Category;
import com.vroomie.car_service.domain.fleet.drivinglog.enums.Payment;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name="tbl_variable_cost")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @Builder
    public VariableCostEntity(DrivingLogEntity drivingLogEntity, Category category,
                             String reciept, Payment payment, BigDecimal cost, String place) {
        this.drivingLogEntity = drivingLogEntity;
        this.category = category;
        this.reciept = reciept;
        this.payment = payment;
        this.cost = cost;
        this.place = place;
    }
}
