package com.vroomie.car_service.domain.dashboard.projection;

import java.math.BigDecimal;

/**
 * TCO 분석을 위한 프로젝션 인터페이스
 * 차량별 총 소유비용 분석 데이터를 제공합니다.
 */
public interface TcoProjection {

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
     * 취득 비용 (구매/리스/렌트 비용)
     * @return 취득 비용
     */
    BigDecimal getAcquisitionCost();

    /**
     * 운영 비용 (연료, 주차, 통행료 등)
     * @return 운영 비용
     */
    BigDecimal getOperationCost();

    /**
     * 유지보수 비용 (정비, 수리, 세차 등)
     * @return 유지보수 비용
     */
    BigDecimal getMaintenanceCost();

    /**
     * 처분 비용 (매각시 손실 등)
     * @return 처분 비용
     */
    BigDecimal getDisposalCost();

    /**
     * 총 비용 (모든 비용의 합계)
     * @return 총 소유비용
     */
    BigDecimal getTotalCost();

    /**
     * 분석 연도
     * @return 연도
     */
    Integer getYear();

    /**
     * 분석 월
     * @return 월 (월별 분석시에만 사용)
     */
    Integer getMonth();
}