package com.vroomie.car_service.domain.dashboard.projection;

import java.math.BigDecimal;

/**
 * 유지보수 비용 분석을 위한 프로젝션 인터페이스
 * 유지보수 관련 비용을 비용 유형별로 분류하여 분석 데이터를 제공합니다.
 */
public interface MaintenanceBreakdownProjection {

    /**
     * 유지보수 비용 유형 (maintenance, repair, inspection 등)
     * @return 비용 유형 ID
     */
    String getFeeType();

    /**
     * 해당 비용 유형의 총 금액
     * @return 비용 금액
     */
    BigDecimal getAmount();

    /**
     * 데이터 소스 식별자
     * @return 데이터 소스
     */
    String getSource();

    /**
     * 해당 비용 유형의 발생 건수 (선택적)
     * @return 발생 건수, 없으면 null
     */
    default Long getIncidentCount() {
        return null;
    }
}