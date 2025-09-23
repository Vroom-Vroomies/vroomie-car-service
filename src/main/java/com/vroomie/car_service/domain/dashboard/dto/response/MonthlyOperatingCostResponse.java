package com.vroomie.car_service.domain.dashboard.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

/**
 * 월별 운영 비용 응답 DTO
 * 회사의 월별 운영비용 분석 결과를 제공합니다.
 */
@Getter
@Builder
public class MonthlyOperatingCostResponse {

    /** 조회 기간 (month, quarter, year 등) */
    private String period;

    /** 조회 날짜 범위 */
    private DateRange dateRange;

    /** 월별 비용 데이터 리스트 */
    private List<MonthlyData> monthlyData;

    /** 비용 요약 정보 */
    private CostSummary summary;

    /**
     * 월별 비용 데이터
     */
    @Getter
    @Builder
    public static class MonthlyData {

        /** 월 정보 (YYYY-MM 형식) */
        private String month;

        /** 고정 비용 (계약료, 보험료 등) */
        private BigDecimal fixedCost;

        /** 변동 비용 리스트 (연료비, 정비비 등) */
        private List<VariableCostItem> variableCost;

        /** 해당 월 총 비용 */
        private BigDecimal total;

        /** 전월 대비 증감액 */
        private BigDecimal previousMonthDiff;
    }

    /**
     * 변동 비용 항목
     */
    @Getter
    @Builder
    public static class VariableCostItem {

        /** 비용 유형 ID (fuel, maintenance 등) */
        private String feeTypeId;

        /** 비용 금액 */
        private BigDecimal amount;

        /** UI 표시용 색상 코드 */
        private String color;
    }

    /**
     * 조회 날짜 범위
     */
    @Getter
    @Builder
    public static class DateRange {

        /** 조회 시작일 (YYYY-MM-DD 형식) */
        private String startDate;

        /** 조회 종료일 (YYYY-MM-DD 형식) */
        private String endDate;
    }

    /**
     * 비용 요약 정보
     */
    @Getter
    @Builder
    public static class CostSummary {

        /** 전체 기간 총 비용 */
        private BigDecimal totalCost;

        /** 월 평균 비용 */
        private BigDecimal averageMonthly;

        /** 비용 증감 추세 (INCREASE, DECREASE, STABLE) */
        private String trend;

        /** 추세 변화율 (%) */
        private BigDecimal trendPercentage;

        /** 가장 비용이 높은 월 */
        private String peakMonth;

        /** 가장 비용이 낮은 월 */
        private String lowestMonth;
    }
}