package com.vroomie.car_service.domain.dashboard.projection;

import java.math.BigDecimal;

/**
 * 사용 중인 비용 유형을 발견하기 위한 프로젝션 인터페이스
 * 실제 데이터에서 동적으로 비용 유형을 추출하여 시스템에서 활용 가능한 비용 유형을 식별합니다.
 */
public interface AvailableFeeTypeProjection {

    /**
     * 실제 데이터에서 발견된 비용 유형
     * @return 비용 유형 ID
     */
    String getFeeType();

    /**
     * 해당 비용 유형이 발견된 데이터 소스
     * @return 데이터 소스 식별자
     */
    String getSource();

    /**
     * 해당 비용 유형의 총 누적 금액
     * @return 총 금액
     */
    BigDecimal getTotalAmount();
}