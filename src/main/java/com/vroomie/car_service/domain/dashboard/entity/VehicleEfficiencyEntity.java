package com.vroomie.car_service.domain.dashboard.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 차량 효율성 메트릭 엔티티
 * 차량별 효율성 지표를 기간별로 저장하고 분석하는 엔티티입니다.
 */
@Entity
@Table(name = "tbl_vehicle_efficiency")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class VehicleEfficiencyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 회사 ID
     */
    @Column(name = "company_id", nullable = false)
    private Long companyId;

    /**
     * 차량 ID
     */
    @Column(name = "vehicle_id", nullable = false)
    private String vehicleId;

    /**
     * 분석 기간 유형 (월별, 연간)
     */
    @Column(name = "analysis_period", nullable = false)
    @Enumerated(EnumType.STRING)
    private AnalysisPeriod period;

    /**
     * 분석 연도
     */
    @Column(name = "year", nullable = false)
    private Integer year;

    /**
     * 분석 월 (월별 분석시에만 사용)
     */
    @Column(name = "month")
    private Integer month;

    /**
     * 비효율성 점수 (0-100점, 높을수록 비효율적)
     */
    @Column(name = "inefficiency_score", precision = 5, scale = 2)
    private BigDecimal inefficiencyScore;

    /**
     * 총 비용
     */
    @Column(name = "total_cost", precision = 15, scale = 2)
    private BigDecimal totalCost;

    /**
     * 총 주행거리 (km)
     */
    @Column(name = "total_distance", precision = 10, scale = 2)
    private BigDecimal totalDistance;

    /**
     * km당 비용
     */
    @Column(name = "cost_per_km", precision = 8, scale = 2)
    private BigDecimal costPerKm;

    /**
     * 사용 일수
     */
    @Column(name = "usage_days")
    private Integer usageDays;

    /**
     * 연비 효율성 (km/L)
     */
    @Column(name = "fuel_efficiency", precision = 8, scale = 2)
    private BigDecimal fuelEfficiency;

    /**
     * 유지보수 발생 빈도
     */
    @Column(name = "maintenance_frequency")
    private Integer maintenanceFrequency;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * 분석 기간 열거형
     */
    public enum AnalysisPeriod {
        MONTH, YEAR
    }
}