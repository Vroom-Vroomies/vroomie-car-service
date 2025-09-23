package com.vroomie.car_service.domain.dashboard.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

/**
 * 유지보수 비용 분석 응답 DTO
 * 유지보수 비용을 비용 유형별로 분석한 결과를 제공합니다.
 */
@Getter
@Builder
public class MaintenanceBreakdownResponse {

    /** 총 유지보수 비용 */
    private BigDecimal total;

    /** 분석 기간 */
    private String period;

    /** 분석 날짜 */
    private String date;

    /** 유지보수 비용 분석 항목들 */
    private List<MaintenanceBreakdownItem> breakdown;

    /** 비용 추세 정보 */
    private MaintenanceTrends trends;

    /**
     * 유지보수 비용 분석 항목
     */
    @Getter
    @Builder
    public static class MaintenanceBreakdownItem {

        /** 비용 유형 ID (maintenance, repair, inspection 등) */
        private String feeTypeId;

        /** 비용 유형 표시명 */
        private String feeTypeName;

        /** 해당 유형의 비용 금액 */
        private BigDecimal amount;

        /** 전체 유지보수 비용에서의 비중 (%) */
        private BigDecimal percentage;

        /** UI 표시용 색상 */
        private String color;

        /** 전월 대비 증감률 (%) */
        private BigDecimal changeRate;

        /** 증감 상태 (INCREASE, DECREASE, STABLE) */
        private String changeStatus;

        /** 해당 유형의 평균 건당 비용 */
        private BigDecimal averagePerIncident;

        /** 해당 유형의 총 발생 건수 */
        private Integer incidentCount;
    }

    /**
     * 유지보수 비용 추세 정보
     */
    @Getter
    @Builder
    public static class MaintenanceTrends {

        /** 지난 3개월 월별 총 유지보수 비용 */
        private List<MonthlyTrend> monthlyTrends;

        /** 전월 대비 총 증감률 (%) */
        private BigDecimal totalChangeRate;

        /** 가장 증가한 비용 유형 */
        private String mostIncreasedType;

        /** 가장 감소한 비용 유형 */
        private String mostDecreasedType;

        /** 예측 다음 달 유지보수 비용 */
        private BigDecimal predictedNextMonth;

        /** 예측 신뢰도 (%) */
        private BigDecimal predictionConfidence;
    }

    /**
     * 월별 추세 데이터
     */
    @Getter
    @Builder
    public static class MonthlyTrend {

        /** 월 (YYYY-MM 형식) */
        private String month;

        /** 해당 월 총 유지보수 비용 */
        private BigDecimal totalAmount;

        /** 전월 대비 증감률 (%) */
        private BigDecimal changeRate;

        /** 주요 비용 발생 유형 */
        private String primaryCostType;
    }
}