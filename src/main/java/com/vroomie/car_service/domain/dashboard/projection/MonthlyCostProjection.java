package com.vroomie.car_service.domain.dashboard.projection;

import java.math.BigDecimal;

/**
 * 월별 비용 데이터를 조회하기 위한 프로젝션 인터페이스
 * 다양한 데이터 소스(변동비용, 계약, 보험)에서 월별 비용 정보를 추출합니다.
 */
public interface MonthlyCostProjection {

    /**
     * 비용이 발생한 월 (YYYY-MM 형식)
     * @return 월 정보
     */
    String getMonth();

    /**
     * 비용 유형 (fuel, maintenance, contract 등)
     * @return 비용 유형 ID
     */
    String getFeeType();

    /**
     * 해당 월의 비용 금액
     * @return 비용 금액
     */
    BigDecimal getAmount();

    /**
     * 데이터 소스 ('contract', 'insurance', 'variable_cost' 등)
     * @return 데이터 소스 식별자
     */
    String getSource();
}