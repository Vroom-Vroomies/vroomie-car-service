package com.vroomie.car_service.domain.fleet.repair.entity;

import com.vroomie.car_service.domain.employee.entity.EmployeeEntity;
import com.vroomie.car_service.domain.fleet.car.entity.CarEntity;
import com.vroomie.car_service.domain.fleet.repair.dto.RepairCreateRequestDTO;
import com.vroomie.car_service.domain.fleet.repair.enums.RepairStatus;
import com.vroomie.car_service.domain.fleet.repair.enums.RepairType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_repair")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RepairEntity {

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
    private RepairType type;

    private String detail;

    @Enumerated(EnumType.STRING)
    private RepairStatus status;

    private LocalDate startedAt;
    private LocalDate endedAt;
    private BigDecimal cost;
    private boolean isSaved;

    @OneToMany(mappedBy = "repair", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<RepairImageEntity> repairImages = new ArrayList<>();

    @Builder
    public RepairEntity(CarEntity car, EmployeeEntity employee, RepairType type, String detail, RepairStatus status, LocalDate startedAt, LocalDate endedAt, BigDecimal cost, boolean isSaved) {
        this.car = car;
        this.employee = employee;
        this.type = type;
        this.detail = detail;
        this.status = status;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.cost = cost;
        this.isSaved = isSaved;
    }

    public void update(RepairCreateRequestDTO dto) {
        if (dto.getType() != null) this.type = RepairType.valueOf(dto.getType());
        if (dto.getDetail() != null) this.detail = dto.getDetail();
        if (dto.getStatus() != null) this.status = RepairStatus.valueOf(dto.getStatus());
        if (dto.getStartedAt() != null) this.startedAt = dto.getStartedAt();
        if (dto.getEndedAt() != null) this.endedAt = dto.getEndedAt();
        if (dto.getCost() != null) this.cost = dto.getCost();
    }

    public void finalizeRepair() {
        this.isSaved = true;
    }

    public void addImage(RepairImageEntity image) {
        this.repairImages.add(image);
        image.setRepair(this);
    }
}