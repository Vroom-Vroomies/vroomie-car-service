package com.vroomie.car_service.domain.dashboard.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

/**
 * 비용 유형 관리 응답 DTO
 * 동적으로 발견된 비용 유형들의 메타데이터와 설정 정보를 제공합니다.
 */
@Getter
@Builder
public class FeeTypeResponse {

    /** 비용 유형 데이터 리스트 */
    private List<FeeTypeData> feeTypes;

    /** 차트 표시 설정 */
    private ChartSettings chartSettings;

    /** 비용 유형 통계 정보 */
    private FeeTypeStatistics statistics;

    /**
     * 비용 유형 데이터
     */
    @Getter
    @Builder
    public static class FeeTypeData {

        /** 비용 유형 고유 식별자 */
        private String id;

        /** 시스템에서 사용되는 이름 */
        private String name;

        /** 화면에 표시될 이름 */
        private String displayName;

        /** 차트 및 UI에서 사용할 색상 코드 */
        private String color;

        /** 비용 카테고리 (FIXED, VARIABLE, MAINTENANCE, OPERATIONAL) */
        private String category;

        /** 현재 사용 중 여부 */
        private Boolean isActive;

        /** 정렬 순서 */
        private Integer sortOrder;

        /** UI 표시용 아이콘 */
        private String icon;

        /** 비용 유형 설명 */
        private String description;

        /** 해당 비용 유형의 총 누적 금액 */
        private BigDecimal totalAmount;

        /** 전체 비용에서의 비중 (%) */
        private BigDecimal percentage;

        /** 데이터 소스 (contract, insurance, variable_cost) */
        private String source;
    }

    /**
     * 차트 표시 설정
     */
    @Getter
    @Builder
    public static class ChartSettings {

        /** 사용 가능한 색상 팔레트 */
        private List<String> colorPalette;

        /** 기본 차트 뷰 타입 (donut, bar, line) */
        private String defaultView;

        /** 차트 애니메이션 사용 여부 */
        private Boolean enableAnimation;

        /** 범례 표시 위치 (top, bottom, left, right) */
        private String legendPosition;

        /** 툴팁 표시 형식 */
        private String tooltipFormat;
    }

    /**
     * 비용 유형 통계 정보
     */
    @Getter
    @Builder
    public static class FeeTypeStatistics {

        /** 전체 비용 유형 수 */
        private Integer totalFeeTypes;

        /** 활성 비용 유형 수 */
        private Integer activeFeeTypes;

        /** 고정 비용 유형 수 */
        private Integer fixedCostTypes;

        /** 변동 비용 유형 수 */
        private Integer variableCostTypes;

        /** 유지보수 비용 유형 수 */
        private Integer maintenanceCostTypes;

        /** 가장 큰 비중을 차지하는 비용 유형 */
        private String dominantFeeType;

        /** 지배적 비용 유형의 비중 (%) */
        private BigDecimal dominantFeeTypeRatio;

        /** 전체 비용 합계 */
        private BigDecimal totalAmount;

        /** 평균 비용 유형당 금액 */
        private BigDecimal averageAmountPerType;
    }
}