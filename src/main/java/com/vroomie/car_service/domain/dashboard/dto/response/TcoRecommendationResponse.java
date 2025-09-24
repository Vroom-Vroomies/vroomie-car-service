package com.vroomie.car_service.domain.dashboard.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.util.List;

/**
 * TCO 기반 권장사항 응답 DTO
 */
@Getter
@Builder
@Jacksonized
public class TcoRecommendationResponse {

    /**
     * 권장사항 요약
     */
    private RecommendationSummary summary;

    /**
     * 차량별 권장사항 목록
     */
    private List<VehicleRecommendation> recommendations;

    /**
     * 페이지네이션 정보
     */
    private TcoAnalysisResponse.PageInfo pageInfo;

    /**
     * 권장사항 요약 정보
     */
    @Getter
    @Builder
    @Jacksonized
    public static class RecommendationSummary {
        /**
         * 총 권장사항 수
         */
        private Long totalRecommendations;

        /**
         * 높은 우선순위 권장사항 수
         */
        private Long highPriorityCount;

        /**
         * 중간 우선순위 권장사항 수
         */
        private Long mediumPriorityCount;

        /**
         * 낮은 우선순위 권장사항 수
         */
        private Long lowPriorityCount;

        /**
         * 총 예상 절감 비용
         */
        private BigDecimal totalEstimatedSaving;

        /**
         * 우선순위별 권장사항 분포
         */
        private List<PriorityDistribution> priorityDistribution;
    }

    /**
     * 우선순위별 분포
     */
    @Getter
    @Builder
    @Jacksonized
    public static class PriorityDistribution {
        /**
         * 우선순위
         */
        private String priority;

        /**
         * 해당 우선순위 권장사항 수
         */
        private Long count;

        /**
         * 전체 대비 비율 (%)
         */
        private Double percentage;

        /**
         * 예상 절감 비용
         */
        private BigDecimal estimatedSaving;
    }

    /**
     * 차량별 권장사항
     */
    @Getter
    @Builder
    @Jacksonized
    public static class VehicleRecommendation {
        /**
         * 차량 ID
         */
        private String vehicleId;

        /**
         * 차량명
         */
        private String vehicleName;

        /**
         * 권장 조치 유형 (disposal, replacement, maintenance)
         */
        private String recommendationType;

        /**
         * 권장 조치명
         */
        private String recommendationTitle;

        /**
         * 권장 사유
         */
        private String reason;

        /**
         * 우선순위 (high, medium, low)
         */
        private String priority;

        /**
         * 예상 절감 비용
         */
        private BigDecimal estimatedSaving;

        /**
         * 현재 비용
         */
        private BigDecimal currentCost;

        /**
         * 절감률 (%)
         */
        private Double savingPercentage;

        /**
         * 차량 연식
         */
        private Integer vehicleAge;

        /**
         * 효율성 점수
         */
        private BigDecimal efficiencyScore;

        /**
         * 권장 조치 상세 정보
         */
        private RecommendationDetail detail;
    }

    /**
     * 권장 조치 상세 정보
     */
    @Getter
    @Builder
    @Jacksonized
    public static class RecommendationDetail {
        /**
         * 예상 실행 기간 (개월)
         */
        private Integer estimatedDurationMonths;

        /**
         * 예상 투자 비용
         */
        private BigDecimal estimatedInvestmentCost;

        /**
         * ROI (투자수익률) %
         */
        private Double roi;

        /**
         * 추가 고려사항
         */
        private List<String> considerations;

        /**
         * 대안 방안
         */
        private List<String> alternatives;
    }
}