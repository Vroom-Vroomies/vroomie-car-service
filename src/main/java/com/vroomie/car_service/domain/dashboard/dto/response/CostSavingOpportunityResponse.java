package com.vroomie.car_service.domain.dashboard.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 비용 절감 기회 분석 응답 DTO
 */
@Getter
@Builder
@Jacksonized
public class CostSavingOpportunityResponse {

    /**
     * 절감 기회 요약
     */
    private SavingOpportunitySummary summary;

    /**
     * 카테고리별 절감 기회 목록
     */
    private List<CategoryOpportunity> opportunities;

    /**
     * 분석 기간
     */
    private AnalysisPeriod analysisPeriod;

    /**
     * 절감 기회 요약 정보
     */
    @Getter
    @Builder
    @Jacksonized
    public static class SavingOpportunitySummary {
        /**
         * 총 절감 가능 금액
         */
        private BigDecimal totalSavingAmount;

        /**
         * 현재 총 비용
         */
        private BigDecimal totalCurrentAmount;

        /**
         * 전체 절감률 (%)
         */
        private Double totalSavingPercentage;

        /**
         * 절감 기회 카테고리 수
         */
        private Integer opportunityCount;

        /**
         * 최고 절감 기회 카테고리
         */
        private String topOpportunityCategory;

        /**
         * 최고 절감 금액
         */
        private BigDecimal topSavingAmount;
    }

    /**
     * 카테고리별 절감 기회
     */
    @Getter
    @Builder
    @Jacksonized
    public static class CategoryOpportunity {
        /**
         * 카테고리 (OPERATIONAL, MAINTENANCE, VARIABLE 등)
         */
        private String category;

        /**
         * 카테고리명
         */
        private String categoryName;

        /**
         * 잠재적 절감 가능 비율 (%)
         */
        private BigDecimal potentialSaving;

        /**
         * 측정 단위 (percentage, amount 등)
         */
        private String unit;

        /**
         * 절감 기회 설명
         */
        private String description;

        /**
         * 예상 절감 금액
         */
        private BigDecimal estimatedAmount;

        /**
         * 현재 비용 금액
         */
        private BigDecimal currentAmount;

        /**
         * 절감률 (%)
         */
        private Double savingPercentage;

        /**
         * 우선순위 (1-5, 1이 가장 높음)
         */
        private Integer priority;

        /**
         * 실행 난이도 (EASY, MEDIUM, HARD)
         */
        private String implementationDifficulty;

        /**
         * 예상 실행 기간 (개월)
         */
        private Integer estimatedImplementationMonths;

        /**
         * 상세 절감 방안
         */
        private List<SavingMethod> savingMethods;
    }

    /**
     * 절감 방안 상세
     */
    @Getter
    @Builder
    @Jacksonized
    public static class SavingMethod {
        /**
         * 절감 방안명
         */
        private String methodName;

        /**
         * 절감 방안 설명
         */
        private String description;

        /**
         * 예상 절감 금액
         */
        private BigDecimal estimatedSaving;

        /**
         * 필요 투자 비용
         */
        private BigDecimal requiredInvestment;

        /**
         * ROI (투자수익률) %
         */
        private Double roi;

        /**
         * 실행 단계
         */
        private List<String> implementationSteps;
    }

    /**
     * 분석 기간 정보
     */
    @Getter
    @Builder
    @Jacksonized
    public static class AnalysisPeriod {
        /**
         * 분석 시작일
         */
        private LocalDate startDate;

        /**
         * 분석 종료일
         */
        private LocalDate endDate;

        /**
         * 분석 기간 (일)
         */
        private Long periodDays;

        /**
         * 분석 기간 설명
         */
        private String periodDescription;
    }
}