package com.vroomie.car_service.domain.dashboard.projection;

import java.math.BigDecimal;

/**
 * 비용 절감 기회를 위한 프로젝션 인터페이스
 * 카테고리별 비용 절감 가능성과 예상 효과를 제공합니다.
 */
public interface CostSavingOpportunityProjection {

    /**
     * 비용 카테고리 (OPERATIONAL, MAINTENANCE, VARIABLE 등)
     * @return 비용 카테고리
     */
    String getCategory();

    /**
     * 잠재적 절감 가능 비율 (%)
     * @return 절감 가능 비율
     */
    BigDecimal getPotentialSaving();

    /**
     * 측정 단위 (percentage, amount 등)
     * @return 측정 단위
     */
    String getUnit();

    /**
     * 절감 기회 설명
     * @return 절감 방법 및 설명
     */
    String getDescription();

    /**
     * 예상 절감 금액
     * @return 절감 가능한 예상 금액
     */
    BigDecimal getEstimatedAmount();

    /**
     * 현재 비용 금액
     * @return 현재 해당 카테고리 총 비용
     */
    BigDecimal getCurrentAmount();
}