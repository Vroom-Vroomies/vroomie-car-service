package com.vroomie.car_service.domain.dashboard.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 효율성 트렌드 분석 응답 DTO
 */
@Getter
@Builder
@Jacksonized
public class EfficiencyTrendResponse {

    /**
     * 트렌드 분석 요약
     */
    private TrendSummary summary;

    /**
     * 차량별 트렌드 데이터
     */
    private List<VehicleTrendData> vehicleTrends;

    /**
     * 월별 회사 전체 트렌드
     */
    private List<MonthlyTrendData> monthlyTrends;

    /**
     * 분석 기간
     */
    private TrendAnalysisPeriod analysisPeriod;

    /**
     * 트렌드 분석 요약
     */
    @Getter
    @Builder
    @Jacksonized
    public static class TrendSummary {
        /**
         * 분석 대상 차량 수
         */
        private Long totalVehicles;

        /**
         * 개선된 차량 수
         */
        private Long improvedVehicles;

        /**
         * 악화된 차량 수
         */
        private Long deterioratedVehicles;

        /**
         * 안정적인 차량 수
         */
        private Long stableVehicles;

        /**
         * 전체 개선률 (%)
         */
        private Double overallImprovementRate;

        /**
         * 평균 효율성 변화 점수
         */
        private BigDecimal averageEfficiencyChange;

        /**
         * 최대 개선 차량 ID
         */
        private String mostImprovedVehicleId;

        /**
         * 최대 개선 점수
         */
        private BigDecimal maxImprovementScore;
    }

    /**
     * 차량별 트렌드 데이터
     */
    @Getter
    @Builder
    @Jacksonized
    public static class VehicleTrendData {
        /**
         * 차량 ID
         */
        private String vehicleId;

        /**
         * 차량명
         */
        private String vehicleName;

        /**
         * 현재 기간 효율성 점수
         */
        private BigDecimal currentEfficiencyScore;

        /**
         * 이전 기간 효율성 점수
         */
        private BigDecimal previousEfficiencyScore;

        /**
         * 효율성 점수 변화
         */
        private BigDecimal efficiencyScoreChange;

        /**
         * 변화율 (%)
         */
        private Double changePercentage;

        /**
         * 트렌드 방향 (IMPROVING, DECLINING, STABLE)
         */
        private String trendDirection;

        /**
         * 개선 순위 (1이 가장 많이 개선)
         */
        private Integer improvementRank;

        /**
         * 연속 개선 기간 (개월)
         */
        private Integer consecutiveImprovementMonths;

        /**
         * 예상 미래 점수 (다음 달 예측)
         */
        private BigDecimal predictedNextScore;
    }

    /**
     * 월별 트렌드 데이터
     */
    @Getter
    @Builder
    @Jacksonized
    public static class MonthlyTrendData {
        /**
         * 연도
         */
        private Integer year;

        /**
         * 월
         */
        private Integer month;

        /**
         * 해당 월 날짜
         */
        private LocalDate date;

        /**
         * 평균 효율성 점수
         */
        private BigDecimal averageEfficiencyScore;

        /**
         * 평균 km당 비용
         */
        private BigDecimal averageCostPerKm;

        /**
         * 평균 연비 효율성
         */
        private BigDecimal averageFuelEfficiency;

        /**
         * 분석 대상 차량 수
         */
        private Long vehicleCount;

        /**
         * 전월 대비 효율성 변화
         */
        private BigDecimal efficiencyChange;

        /**
         * 전월 대비 변화율 (%)
         */
        private Double changePercentage;

        /**
         * 해당 월 주요 이벤트 (선택사항)
         */
        private List<String> significantEvents;
    }

    /**
     * 트렌드 분석 기간
     */
    @Getter
    @Builder
    @Jacksonized
    public static class TrendAnalysisPeriod {
        /**
         * 현재 기간 시작일
         */
        private LocalDate currentPeriodStart;

        /**
         * 현재 기간 종료일
         */
        private LocalDate currentPeriodEnd;

        /**
         * 이전 기간 시작일
         */
        private LocalDate previousPeriodStart;

        /**
         * 이전 기간 종료일
         */
        private LocalDate previousPeriodEnd;

        /**
         * 분석 기간 설명
         */
        private String periodDescription;

        /**
         * 비교 기간 유형 (MONTH_TO_MONTH, QUARTER_TO_QUARTER, YEAR_TO_YEAR)
         */
        private String comparisonType;
    }
}