package com.vroomie.car_service.domain.fleet.inspection.entity;

import com.vroomie.car_service.domain.employee.entity.EmployeeEntity;
import com.vroomie.car_service.domain.fleet.car.entity.CarEntity;
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
}