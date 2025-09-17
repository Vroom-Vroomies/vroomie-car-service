package com.vroomie.car_service.domain.fleet.accident.entity;

import com.vroomie.car_service.domain.employee.entity.EmployeeEntity;
import com.vroomie.car_service.domain.fleet.accident.dto.AccidentCreateRequestDTO;
import com.vroomie.car_service.domain.fleet.accident.enums.AccidentType;
import com.vroomie.car_service.domain.fleet.car.entity.CarEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "tbl_accident")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccidentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id", nullable = false)
    private CarEntity car;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emp_email", nullable = false)
    private EmployeeEntity employee;

    @Enumerated(EnumType.STRING)
    private AccidentType type;

    private String note;
    private String detail;
    private LocalDate occurredAt;
    private BigDecimal cost;
    private boolean isSaved;

    @Builder
    public AccidentEntity(CarEntity car, EmployeeEntity employee, AccidentType type, String note, String detail, LocalDate occurredAt, BigDecimal cost, boolean isSaved) {
        this.car = car;
        this.employee = employee;
        this.type = type;
        this.note = note;
        this.detail = detail;
        this.occurredAt = occurredAt;
        this.cost = cost;
        this.isSaved = isSaved;
    }

    public void finalize() {
        this.isSaved = true;
    }
}
