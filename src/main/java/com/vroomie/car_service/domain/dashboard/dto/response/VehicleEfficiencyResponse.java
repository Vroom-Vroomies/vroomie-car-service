package com.vroomie.car_service.domain.dashboard.dto.response;

import com.vroomie.car_service.domain.dashboard.entity.VehicleEfficiencyEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.util.List;

/**
 * 차량 효율성 분석 응답 DTO
 */
@Getter
@Builder
@Jacksonized
public class VehicleEfficiencyResponse {

    /**
     * 효율성 분석 요약
     */
    private EfficiencySummary summary;

    /**
     * 차량별 효율성 데이터
     */
    private List<VehicleEfficiencyData> vehicles;

    /**
     * 효율성 분포 정보
     */
    private List<EfficiencyDistribution> distribution;

    /**
     * 페이지네이션 정보
     */
    private TcoAnalysisResponse.PageInfo pageInfo;

    /**
     * 효율성 분석 요약
     */
    @Getter
    @Builder
    @Jacksonized
    public static class EfficiencySummary {
        /**
         * 전체 차량 수
         */
        private Long totalVehicles;

        /**
         * 평균 비효율성 점수
         */
        private BigDecimal averageInefficiencyScore;

        /**
         * 평균 km당 비용
         */
        private BigDecimal averageCostPerKm;

        /**
         * 평균 연비 효율성
         */
        private BigDecimal averageFuelEfficiency;

        /**
         * 개선 필요 차량 수
         */
        private Long vehiclesNeedingImprovement;

        /**
         * 개선 필요 비율 (%)
         */
        private Double improvementNeededPercentage;

        /**
         * 분석 기간
         */
        private VehicleEfficiencyEntity.AnalysisPeriod period;

        /**
         * 분석 연도
         */
        private Integer year;

        /**
         * 분석 월
         */
        private Integer month;
    }

    /**
     * 차량별 효율성 데이터
     */
    @Getter
    @Builder
    @Jacksonized
    public static class VehicleEfficiencyData {
        /**
         * 차량 ID
         */
        private String vehicleId;

        /**
         * 차량명
         */
        private String vehicleName;

        /**
         * 비효율성 점수 (0-100점, 높을수록 비효율적)
         */
        private BigDecimal inefficiencyScore;

        /**
         * 효율성 등급 (A, B, C, D, F)
         */
        private String efficiencyGrade;

        /**
         * 총 비용
         */
        private BigDecimal totalCost;

        /**
         * 총 주행거리 (km)
         */
        private BigDecimal totalDistance;

        /**
         * km당 비용
         */
        private BigDecimal costPerKm;

        /**
         * 사용 일수
         */
        private Integer usageDays;

        /**
         * 연비 효율성 (km/L)
         */
        private BigDecimal fuelEfficiency;

        /**
         * 유지보수 발생 빈도
         */
        private Integer maintenanceFrequency;

        /**
         * 효율성 개선 여지 (%)
         */
        private Double improvementPotential;

        /**
         * 동종 차량 대비 순위
         */
        private Integer rankAmongSimilar;

        /**
         * 전월 대비 변화
         */
        private EfficiencyTrend monthlyTrend;
    }

    /**
     * 효율성 트렌드 정보
     */
    @Getter
    @Builder
    @Jacksonized
    public static class EfficiencyTrend {
        /**
         * 비효율성 점수 변화
         */
        private BigDecimal inefficiencyScoreChange;

        /**
         * km당 비용 변화
         */
        private BigDecimal costPerKmChange;

        /**
         * 연비 효율성 변화
         */
        private BigDecimal fuelEfficiencyChange;

        /**
         * 전반적 개선 여부
         */
        private Boolean isImproved;

        /**
         * 변화율 (%)
         */
        private Double changePercentage;

        /**
         * 트렌드 방향 (IMPROVING, DECLINING, STABLE)
         */
        private String trendDirection;
    }

    /**
     * 효율성 분포 정보
     */
    @Getter
    @Builder
    @Jacksonized
    public static class EfficiencyDistribution {
        /**
         * 효율성 등급 (VERY_EFFICIENT, EFFICIENT, MODERATE, INEFFICIENT, VERY_INEFFICIENT)
         */
        private String efficiencyGrade;

        /**
         * 등급명
         */
        private String gradeName;

        /**
         * 해당 등급 차량 수
         */
        private Long vehicleCount;

        /**
         * 전체 대비 비율 (%)
         */
        private Double percentage;

        /**
         * 평균 비효율성 점수
         */
        private BigDecimal avgInefficiencyScore;

        /**
         * 평균 km당 비용
         */
        private BigDecimal avgCostPerKm;

        /**
         * 점수 범위
         */
        private ScoreRange scoreRange;
    }

    /**
     * 점수 범위 정보
     */
    @Getter
    @Builder
    @Jacksonized
    public static class ScoreRange {
        /**
         * 최소 점수
         */
        private BigDecimal minScore;

        /**
         * 최대 점수
         */
        private BigDecimal maxScore;

        /**
         * 범위 설명
         */
        private String description;
    }
}