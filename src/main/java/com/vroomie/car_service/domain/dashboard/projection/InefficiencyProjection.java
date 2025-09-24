package com.vroomie.car_service.domain.dashboard.projection;

import java.math.BigDecimal;

/**
 * 차량 비효율성 분석을 위한 프로젝션 인터페이스
 * 차량별 효율성 지표와 순위 정보를 제공합니다.
 */
public interface InefficiencyProjection {

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
     * 비효율성 점수 (0-100점, 높을수록 비효율적)
     * @return 비효율성 점수
     */
    BigDecimal getInefficiencyScore();

    /**
     * 효율성 순위 (회사 내)
     * @return 순위
     */
    Integer getRank();

    /**
     * 분석 기간 (month, year)
     * @return 분석 기간
     */
    String getPeriod();

    /**
     * 분석 연도
     * @return 연도
     */
    Integer getYear();

    /**
     * 분석 월
     * @return 월
     */
    Integer getMonth();

    /**
     * 총 비용
     * @return 해당 기간 총 비용
     */
    BigDecimal getTotalCost();

    /**
     * 총 주행거리 (km)
     * @return 해당 기간 총 주행거리
     */
    BigDecimal getTotalDistance();

    /**
     * km당 비용
     * @return 해당 차량의 km당 비용
     */
    BigDecimal getCostPerKm();

    /**
     * 평균 km당 비용 (회사 전체)
     * @return 회사 평균 km당 비용
     */
    BigDecimal getAvgCostPerKm();

    /**
     * 사용 일수
     * @return 해당 기간 차량 사용 일수
     */
    Integer getUsageDays();

    /**
     * 연비 효율성 (km/L)
     * @return 연료 효율성
     */
    BigDecimal getFuelEfficiency();

    /**
     * 유지보수 발생 빈도
     * @return 해당 기간 유지보수 발생 횟수
     */
    Integer getMaintenanceFrequency();
}