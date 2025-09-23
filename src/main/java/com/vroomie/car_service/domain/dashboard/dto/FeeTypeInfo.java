package com.vroomie.car_service.domain.dashboard.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * 동적 비용 유형 정보를 담는 DTO
 * 기존 테이블에서 추출된 비용 유형의 메타데이터를 제공합니다.
 */
@Getter
@Builder
public class FeeTypeInfo {

    /** 비용 유형 고유 식별자 */
    private String feeTypeId;

    /** 화면에 표시될 비용 유형명 */
    private String displayName;

    /** 차트 및 UI에서 사용할 색상 코드 */
    private String color;

    /** 비용 유형 카테고리 */
    private FeeTypeCategory category;

    /** UI에서 표시할 아이콘 */
    private String icon;

    /** 비용 유형에 대한 설명 */
    private String description;

    /** 해당 비용 유형의 총 금액 */
    private BigDecimal totalAmount;

    /** 정렬 순서 */
    private Integer sortOrder;

    /**
     * 비용 유형 카테고리 열거형
     * FIXED: 고정 비용, VARIABLE: 변동 비용, MAINTENANCE: 유지 비용, OPERATIONAL: 관리 비용
     */
    public enum FeeTypeCategory {
        /** 고정 비용 (계약료, 보험료 등) */
        FIXED,
        /** 변동 비용 (연료비, 통행료 등) */
        VARIABLE,
        /** 유지보수 비용 (정비비, 수리비 등) */
        MAINTENANCE
    }
}