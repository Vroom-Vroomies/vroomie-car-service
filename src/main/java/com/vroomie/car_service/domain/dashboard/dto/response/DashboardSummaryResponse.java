package com.vroomie.car_service.domain.dashboard.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 대시보드 요약 정보 응답 DTO
 * Phase 3의 모든 주요 지표를 통합하여 제공
 */
@Getter
@Builder
@Jacksonized
public class DashboardSummaryResponse {

    /**
     * TCO 요약 정보
     */
    private TcoSummaryInfo tcoSummary;

    /**
     * 효율성 요약 정보
     */
    private EfficiencySummaryInfo efficiencySummary;

    /**
     * 비용 절감 기회 요약
     */
    private SavingOpportunitySummaryInfo savingOpportunitySummary;

    /**
     * 주요 알림 및 권장사항
     */
    private List<DashboardAlert> alerts;

    /**
     * KPI 지표
     */
    private List<KpiIndicator> kpiIndicators;

    /**
     * 대시보드 생성 시간
     */
    private LocalDateTime generatedAt;

    /**
     * 데이터 업데이트 시간
     */
    private LocalDateTime dataUpdatedAt;

    /**
     * TCO 요약 정보
     */
    @Getter
    @Builder
    @Jacksonized
    public static class TcoSummaryInfo {
        /**
         * 총 차량 수
         */
        private Long totalVehicles;

        /**
         * 월평균 TCO
         */
        private BigDecimal monthlyAverageTco;

        /**
         * 전월 대비 변화율 (%)
         */
        private Double monthOverMonthChange;

        /**
         * 최고 비용 차량 ID
         */
        private String highestCostVehicleId;

        /**
         * 최고 비용
         */
        private BigDecimal highestCost;

        /**
         * TCO 증가 추세 차량 수
         */
        private Long increasingTrendVehicles;
    }

    /**
     * 효율성 요약 정보
     */
    @Getter
    @Builder
    @Jacksonized
    public static class EfficiencySummaryInfo {
        /**
         * 평균 효율성 점수
         */
        private BigDecimal averageEfficiencyScore;

        /**
         * 개선 필요 차량 수
         */
        private Long vehiclesNeedingImprovement;

        /**
         * 개선 필요 비율 (%)
         */
        private Double improvementNeededPercentage;

        /**
         * 최고 효율 차량 ID
         */
        private String mostEfficientVehicleId;

        /**
         * 최저 효율 차량 ID
         */
        private String leastEfficientVehicleId;

        /**
         * 평균 km당 비용
         */
        private BigDecimal averageCostPerKm;
    }

    /**
     * 비용 절감 기회 요약
     */
    @Getter
    @Builder
    @Jacksonized
    public static class SavingOpportunitySummaryInfo {
        /**
         * 총 절감 가능 금액
         */
        private BigDecimal totalSavingPotential;

        /**
         * 절감률 (%)
         */
        private Double savingPercentage;

        /**
         * 최우선 절감 카테고리
         */
        private String topSavingCategory;

        /**
         * 최우선 절감 금액
         */
        private BigDecimal topSavingAmount;

        /**
         * 실행 가능한 절감 기회 수
         */
        private Integer actionableOpportunities;
    }

    /**
     * 대시보드 알림
     */
    @Getter
    @Builder
    @Jacksonized
    public static class DashboardAlert {
        /**
         * 알림 유형 (WARNING, INFO, CRITICAL, SUCCESS)
         */
        private String alertType;

        /**
         * 알림 제목
         */
        private String title;

        /**
         * 알림 메시지
         */
        private String message;

        /**
         * 관련 차량 ID (선택사항)
         */
        private String relatedVehicleId;

        /**
         * 권장 조치
         */
        private String recommendedAction;

        /**
         * 우선순위 (1-5, 1이 가장 높음)
         */
        private Integer priority;

        /**
         * 알림 생성 시간
         */
        private LocalDateTime createdAt;
    }

    /**
     * KPI 지표
     */
    @Getter
    @Builder
    @Jacksonized
    public static class KpiIndicator {
        /**
         * KPI 명
         */
        private String name;

        /**
         * KPI 설명
         */
        private String description;

        /**
         * 현재 값
         */
        private BigDecimal currentValue;

        /**
         * 목표 값
         */
        private BigDecimal targetValue;

        /**
         * 달성률 (%)
         */
        private Double achievementRate;

        /**
         * 전월 대비 변화
         */
        private BigDecimal change;

        /**
         * 변화율 (%)
         */
        private Double changePercentage;

        /**
         * 트렌드 방향 (UP, DOWN, STABLE)
         */
        private String trendDirection;

        /**
         * 단위
         */
        private String unit;

        /**
         * KPI 카테고리 (TCO, EFFICIENCY, COST_SAVING)
         */
        private String category;
    }
}