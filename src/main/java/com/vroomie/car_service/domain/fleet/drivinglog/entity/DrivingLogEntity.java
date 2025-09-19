package com.vroomie.car_service.domain.fleet.drivinglog.entity;

import com.vroomie.car_service.domain.employee.entity.EmployeeEntity;
import com.vroomie.car_service.domain.fleet.car.entity.CarEntity;
import com.vroomie.car_service.domain.fleet.drivinglog.dto.req.DrivingLogEndReqDTO;
import com.vroomie.car_service.domain.fleet.drivinglog.dto.req.DrivingLogReqDTO;
import com.vroomie.car_service.domain.fleet.drivinglog.enums.LogStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.vroomie.car_service.domain.fleet.drivinglog.util.GpsDistanceCalculator.calculateDistance;

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
    private Long startOdometer;
    private String startOdometerImage;
    private Long endOdometer;
    private String endOdometerImage;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private BigDecimal startLat;
    private BigDecimal startLng;
    private BigDecimal endLat;
    private BigDecimal endLng;
    private String startLocation;
    private String endLocation;
    private BigDecimal gpsDistance;
    private BigDecimal odometerDistance;

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
        if (drivingLogEndReqDTO.getEndLat() != null) this.endLat = drivingLogEndReqDTO.getEndLat();
        if (drivingLogEndReqDTO.getEndLng() != null) this.endLng = drivingLogEndReqDTO.getEndLng();
        if (drivingLogEndReqDTO.getEndLocation() != null) this.endLocation = drivingLogEndReqDTO.getEndLocation();
        // 거리 계산 (GPS 기반)
        if (this.startLat != null && this.endLat != null && this.startLng != null && this.endLng != null) {
            this.gpsDistance = calculateDistance(this.startLat, this.startLng, this.endLat, this.endLng);
        }
        // 거리 계산 (계기판 기반)
        if (this.startOdometer != null && this.endOdometer != null) {
            this.odometerDistance = BigDecimal.valueOf(this.endOdometer - this.startOdometer);
        }
        this.logStatus = LogStatus.PENDING;
    }

    // 제출 전 운행일지 전체 업데이트 가능 메소드(관리자 기능)
    public void updateDrivingLogAll(DrivingLogReqDTO drivingLogReqDTO, EmployeeEntity employeeEntity) {
        if (employeeEntity != null) this.employeeEntity = employeeEntity;
        if (drivingLogReqDTO.getStartOdometer() != null) this.startOdometer = drivingLogReqDTO.getStartOdometer();
        if (drivingLogReqDTO.getStartOdometerImage() != null) this.startOdometerImage = drivingLogReqDTO.getStartOdometerImage();
        if (drivingLogReqDTO.getEndOdometer() != null) this.endOdometer = drivingLogReqDTO.getEndOdometer();
        if (drivingLogReqDTO.getEndOdometerImage() != null) this.endOdometerImage = drivingLogReqDTO.getEndOdometerImage();
        if (drivingLogReqDTO.getStartedAt() != null) this.startedAt = drivingLogReqDTO.getStartedAt();
        if (drivingLogReqDTO.getEndedAt() != null) this.endedAt = drivingLogReqDTO.getEndedAt();

        // 거리 계산 (수정된 계기판 기반)
        if (this.startOdometer != null && this.endOdometer != null) {
            this.odometerDistance = BigDecimal.valueOf(this.endOdometer - this.startOdometer);
        }
    }

    // 운행일지 최종제출
    public void submitDrivingLog() {
        this.isSaved = true;
        this.logStatus = LogStatus.COMPLETED;
    }
}
