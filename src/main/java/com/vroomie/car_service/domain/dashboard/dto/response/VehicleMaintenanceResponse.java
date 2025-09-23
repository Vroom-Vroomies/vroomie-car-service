package com.vroomie.car_service.domain.dashboard.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 차량 유지보수 비용 응답 DTO
 * 차량별 유지보수 비용 분석 결과를 제공합니다.
 */
@Getter
@Builder
public class VehicleMaintenanceResponse {

    /** 차량별 유지보수 데이터 리스트 */
    private List<VehicleMaintenanceData> vehicles;

    /** 월별 비교 분석 데이터 */
    private MonthlyComparison monthlyComparison;

    /** 전체 요약 정보 */
    private MaintenanceSummary summary;

    /**
     * 차량별 유지보수 데이터
     */
    @Getter
    @Builder
    public static class VehicleMaintenanceData {

        /** 차량 식별자 (차량 번호) */
        private String vehicleId;

        /** 차량명 (모델명) */
        private String vehicleName;

        /** 비용 유형별 금액 맵 (maintenance: 50000, repair: 30000 등) */
        private Map<String, BigDecimal> costs;

        /** 해당 차량의 총 유지보수 비용 */
        private BigDecimal total;

        /** 비용 발생 기간 (YYYY-MM 형식) */
        private String period;

        /** 평균 대비 비용 수준 (HIGH, NORMAL, LOW) */
        private String costLevel;

        /** 가장 큰 비용 항목 */
        private String majorCostType;

        /** 전월 대비 증감률 (%) */
        private BigDecimal changeRate;
    }

    /**
     * 월별 비교 분석 데이터
     */
    @Getter
    @Builder
    public static class MonthlyComparison {

        /** 이번 달 비용 유형별 데이터 (feeType -> [차량별 비용 리스트]) */
        private Map<String, List<BigDecimal>> thisMonth;

        /** 지난 달 비용 유형별 데이터 (feeType -> [차량별 비용 리스트]) */
        private Map<String, List<BigDecimal>> lastMonth;

        /** 월별 증감률 (%) */
        private BigDecimal monthlyChangeRate;

        /** 가장 증가한 비용 유형 */
        private String mostIncreasedType;

        /** 가장 감소한 비용 유형 */
        private String mostDecreasedType;
    }

    /**
     * 유지보수 비용 전체 요약
     */
    @Getter
    @Builder
    public static class MaintenanceSummary {

        /** 전체 차량 수 */
        private Integer totalVehicles;

        /** 총 유지보수 비용 */
        private BigDecimal totalMaintenanceCost;

        /** 차량당 평균 비용 */
        private BigDecimal averageCostPerVehicle;

        /** 가장 비용이 높은 차량 ID */
        private String highestCostVehicle;

        /** 가장 비용이 낮은 차량 ID */
        private String lowestCostVehicle;

        /** 주요 비용 유형 (가장 큰 비중을 차지하는 유형) */
        private String primaryCostType;

        /** 주요 비용 유형의 비중 (%) */
        private BigDecimal primaryCostRatio;
    }
}