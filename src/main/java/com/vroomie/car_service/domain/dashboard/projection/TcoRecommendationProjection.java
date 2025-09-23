package com.vroomie.car_service.domain.dashboard.projection;

import java.math.BigDecimal;

/**
 * TCO 기반 권장사항을 위한 프로젝션 인터페이스
 * 차량별 권장 조치 및 비용 절감 기회를 제공합니다.
 */
public interface TcoRecommendationProjection {

    /**
     * 차량 ID
     * @return 차량 번호
     */
    String getVehicleId();

    /**
     * 차량명
     * @return 차량 모델명
     */
    String getVehicleName();

    /**
     * 권장 조치 유형 (disposal, replacement, maintenance)
     * @return 권장 조치 유형
     */
    String getRecommendationType();

    /**
     * 권장 사유
     * @return 권장 사유 설명
     */
    String getReason();

    /**
     * 우선순위 (high, medium, low)
     * @return 우선순위
     */
    String getPriority();

    /**
     * 예상 절감 비용
     * @return 예상 절감 금액
     */
    BigDecimal getEstimatedSaving();

    /**
     * 현재 비용
     * @return 현재 총 비용
     */
    BigDecimal getCurrentCost();

    /**
     * 차량 연식 (현재년도 - 차량년도)
     * @return 차량 연식
     */
    Integer getVehicleAge();

    /**
     * 효율성 점수
     * @return 비효율성 점수
     */
    BigDecimal getEfficiencyScore();
}