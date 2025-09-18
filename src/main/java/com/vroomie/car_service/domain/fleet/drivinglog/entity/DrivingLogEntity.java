package com.vroomie.car_service.domain.fleet.drivinglog.entity;

import com.vroomie.car_service.domain.employee.entity.EmployeeEntity;
import com.vroomie.car_service.domain.fleet.car.entity.CarEntity;
import com.vroomie.car_service.domain.fleet.drivinglog.dto.DrivingLogEndReqDTO;
import com.vroomie.car_service.domain.fleet.drivinglog.dto.DrivingLogReqDTO;
import com.vroomie.car_service.domain.fleet.drivinglog.enums.LogStatus;
import com.vroomie.car_service.domain.fleet.drivinglog.enums.Purpose;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_driving_log")
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
public class DrivingLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emp_email")
    private EmployeeEntity employeeEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id")
    private CarEntity carEntity;

    @Enumerated(EnumType.STRING)
    private Purpose purpose;
    private String detail;
    private Long startOdometer;
    private String startOdometerImage;
    private Long endOdometer;
    private String endOdometerImage;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

    @Enumerated(EnumType.STRING)
    private LogStatus logStatus;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
    private Boolean isSaved;

    // 종료 시점 운행일지 업데이트 메소드
    public void updateDrivingLogEnd(DrivingLogEndReqDTO drivingLogEndReqDTO, EmployeeEntity employeeEntity) {
        if (employeeEntity != null) this.employeeEntity = employeeEntity;
        if (drivingLogEndReqDTO.getEndOdometer() != null) this.endOdometer = drivingLogEndReqDTO.getEndOdometer();
        if (drivingLogEndReqDTO.getEndOdometerImage() != null) this.endOdometerImage = drivingLogEndReqDTO.getEndOdometerImage();
        this.endedAt = LocalDateTime.now();
        this.logStatus = LogStatus.PENDING;
    }

    // 제출 전 운행일지 전체 업데이트 가능 메소드(관리자 기능)
    public void updateDrivingLogAll(DrivingLogReqDTO drivingLogReqDTO, EmployeeEntity employeeEntity) {
        if (employeeEntity != null) this.employeeEntity = employeeEntity;
        if (drivingLogReqDTO.getPurpose() != null) this.purpose = drivingLogReqDTO.getPurpose();
        if (drivingLogReqDTO.getDetail() != null) this.detail = drivingLogReqDTO.getDetail();
        if (drivingLogReqDTO.getStartOdometer() != null) this.startOdometer = drivingLogReqDTO.getStartOdometer();
        if (drivingLogReqDTO.getStartOdometerImage() != null) this.startOdometerImage = drivingLogReqDTO.getStartOdometerImage();
        if (drivingLogReqDTO.getEndOdometer() != null) this.endOdometer = drivingLogReqDTO.getEndOdometer();
        if (drivingLogReqDTO.getEndOdometerImage() != null) this.endOdometerImage = drivingLogReqDTO.getEndOdometerImage();
        if (drivingLogReqDTO.getStartedAt() != null) this.startedAt = drivingLogReqDTO.getStartedAt();
        if (drivingLogReqDTO.getEndedAt() != null) this.endedAt = drivingLogReqDTO.getEndedAt();
    }

    // 운행일지 최종제출
    public void submitDrivingLog() {
        this.isSaved = true;
        this.logStatus = LogStatus.COMPLETED;
    }
}
