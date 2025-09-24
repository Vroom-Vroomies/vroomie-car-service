package com.vroomie.car_service.domain.dashboard.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * TCO(총 소유비용) 분석 엔티티
 * 차량별/회사별 총 소유비용을 기간별로 저장하고 분석하는 엔티티입니다.
 */
@Entity
@Table(name = "tbl_tco_analysis")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TcoAnalysisEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 회사 ID
     */
    @Column(name = "company_id", nullable = false)
    private Long companyId;

    /**
     * 차량 ID (특정 차량 분석시 사용, null이면 전체 회사 분석)
     */
    @Column(name = "vehicle_id")
    private String vehicleId;

    /**
     * 분석 기간 유형 (월별, 분기별, 연간)
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
     * 취득 비용 (구매/리스/렌트 비용)
     */
    @Column(name = "acquisition_cost", precision = 15, scale = 2)
    private BigDecimal acquisitionCost;

    /**
     * 운영 비용 (연료, 주차, 통행료 등)
     */
    @Column(name = "operation_cost", precision = 15, scale = 2)
    private BigDecimal operationCost;

    /**
     * 유지보수 비용 (정비, 수리, 세차 등)
     */
    @Column(name = "maintenance_cost", precision = 15, scale = 2)
    private BigDecimal maintenanceCost;

    /**
     * 처분 비용 (매각시 손실 등)
     * TODO: 현재 0으로 설정, 향후 외부 시세 API 연동시 감가상각 계산 추가
     */
    @Column(name = "disposal_cost", precision = 15, scale = 2)
    private BigDecimal disposalCost;

    /**
     * 총 비용 (모든 비용의 합계)
     */
    @Column(name = "total_cost", precision = 15, scale = 2)
    private BigDecimal totalCost;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * 분석 기간 열거형
     */
    public enum AnalysisPeriod {
        MONTHLY, QUARTERLY, ANNUAL
    }
}