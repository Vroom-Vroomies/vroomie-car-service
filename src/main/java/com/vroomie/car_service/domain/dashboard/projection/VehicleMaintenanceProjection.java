package com.vroomie.car_service.domain.dashboard.projection;

import java.math.BigDecimal;

/**
 * 차량별 유지보수 비용 데이터를 조회하기 위한 프로젝션 인터페이스
 * 특정 차량의 유지보수 관련 비용을 비용 유형별로 추출합니다.
 */
public interface VehicleMaintenanceProjection {

    /**
     * 차량 식별자 (차량 번호)
     * @return 차량 ID
     */
    String getVehicleId();

    /**
     * 차량명 (모델명)
     * @return 차량명
     */
    String getVehicleName();

    /**
     * 비용 유형 (maintenance, repair, inspection 등)
     * @return 비용 유형 ID
     */
    String getFeeType();

    /**
     * 유지보수 비용 금액
     * @return 비용 금액
     */
    BigDecimal getAmount();

    /**
     * 비용 발생 기간 (YYYY-MM 형식)
     * @return 기간 정보
     */
    String getPeriod();

    /**
     * 데이터 소스 식별자
     * @return 데이터 소스
     */
    String getSource();
}