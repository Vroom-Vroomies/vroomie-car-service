package com.vroomie.car_service.domain.fleet.inspection.entity;

import com.vroomie.car_service.domain.employee.entity.EmployeeEntity;
import com.vroomie.car_service.domain.fleet.car.entity.CarEntity;
import com.vroomie.car_service.domain.fleet.inspection.dto.InspectionCreateRequestDTO;
import com.vroomie.car_service.domain.fleet.inspection.enums.InspectionType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_inspection")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class InspectionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id", nullable = false)
    private CarEntity car;

    private LocalDateTime date;
    private String centerName;
    private String centerLocation;
    private String inspectorName;

    @Enumerated(EnumType.STRING)
    private InspectionType inspectionType;

    private String finalResult; // 합격/불합격
    private LocalDate validUntil;
    private String failureReason;
    private String result;
    private String remarks;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private EmployeeEntity employee;

    @Builder
    public InspectionEntity(CarEntity car, LocalDateTime date, String centerName, String centerLocation, String inspectorName, InspectionType inspectionType, String finalResult, LocalDate validUntil, String failureReason, String result, String remarks, LocalDateTime createdAt, LocalDateTime updatedAt, EmployeeEntity employee) {
        this.car = car;
        this.date = date;
        this.centerName = centerName;
        this.centerLocation = centerLocation;
        this.inspectorName = inspectorName;
        this.inspectionType = inspectionType;
        this.finalResult = finalResult;
        this.validUntil = validUntil;
        this.failureReason = failureReason;
        this.result = result;
        this.remarks = remarks;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.employee = employee;
    }

    public void update(InspectionCreateRequestDTO dto) {
        if (dto.getDate() != null) this.date = dto.getDate();
        if (dto.getCenterName() != null) this.centerName = dto.getCenterName();
        if (dto.getCenterLocation() != null) this.centerLocation = dto.getCenterLocation();
        if (dto.getInspectorName() != null) this.inspectorName = dto.getInspectorName();
        if (dto.getInspectionType() != null) this.inspectionType = dto.getInspectionType();
        if (dto.getFinalResult() != null) this.finalResult = dto.getFinalResult();
        if (dto.getValidUntil() != null) this.validUntil = dto.getValidUntil();
        if (dto.getFailureReason() != null) this.failureReason = dto.getFailureReason();
        if (dto.getResult() != null) this.result = dto.getResult();
        if (dto.getRemarks() != null) this.remarks = dto.getRemarks();
    }
}