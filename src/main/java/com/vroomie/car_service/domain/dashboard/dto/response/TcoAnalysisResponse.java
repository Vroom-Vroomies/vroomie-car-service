package com.vroomie.car_service.domain.dashboard.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.util.List;

/**
 * TCO 분석 응답 DTO
 */
@Getter
@Builder
@Jacksonized
public class TcoAnalysisResponse {

    /**
     * TCO 분석 결과 요약
     */
    private TcoSummary summary;

    /**
     * 차량별 TCO 분석 데이터
     */
    private List<TcoVehicleData> vehicles;

    /**
     * 페이지네이션 정보
     */
    private PageInfo pageInfo;

    /**
     * TCO 요약 정보
     */
    @Getter
    @Builder
    @Jacksonized
    public static class TcoSummary {
        /**
         * 총 차량 수
         */
        private Long totalVehicles;

        /**
         * 평균 TCO
         */
        private BigDecimal averageTotalCost;

        /**
         * 최고 TCO
         */
        private BigDecimal maxTotalCost;

        /**
         * 최저 TCO
         */
        private BigDecimal minTotalCost;

        /**
         * 회사 전체 TCO
         */
        private BigDecimal totalCompanyCost;

        /**
         * 분석 기간
         */
        private String analysisPeriod;
    }

    /**
     * 차량별 TCO 데이터
     */
    @Getter
    @Builder
    @Jacksonized
    public static class TcoVehicleData {
        /**
         * 차량 ID
         */
        private String vehicleId;

        /**
         * 차량명
         */
        private String vehicleName;

        /**
         * 취득 비용
         */
        private BigDecimal acquisitionCost;

        /**
         * 운영 비용
         */
        private BigDecimal operationCost;

        /**
         * 유지보수 비용
         */
        private BigDecimal maintenanceCost;

        /**
         * 처분 비용
         */
        private BigDecimal disposalCost;

        /**
         * 총 비용
         */
        private BigDecimal totalCost;

        /**
         * 비용 구성 비율
         */
        private CostBreakdown costBreakdown;

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
     * 비용 구성 비율
     */
    @Getter
    @Builder
    @Jacksonized
    public static class CostBreakdown {
        /**
         * 취득비 비율 (%)
         */
        private Double acquisitionPercentage;

        /**
         * 운영비 비율 (%)
         */
        private Double operationPercentage;

        /**
         * 유지보수비 비율 (%)
         */
        private Double maintenancePercentage;

        /**
         * 처분비 비율 (%)
         */
        private Double disposalPercentage;
    }

    /**
     * 페이지네이션 정보
     */
    @Getter
    @Builder
    @Jacksonized
    public static class PageInfo {
        /**
         * 현재 페이지 번호
         */
        private Integer currentPage;

        /**
         * 페이지 크기
         */
        private Integer pageSize;

        /**
         * 총 요소 수
         */
        private Long totalElements;

        /**
         * 총 페이지 수
         */
        private Integer totalPages;

        /**
         * 첫 번째 페이지 여부
         */
        private Boolean isFirst;

        /**
         * 마지막 페이지 여부
         */
        private Boolean isLast;
    }
}