package com.vroomie.car_service.domain.dashboard.projection;

/**
 * 차량 보유 현황 프로젝션
 * - 대시보드에서 차량 종류별 보유 현황 조회를 위한 프로젝션
 */
public interface VehicleOwnershipProjection {
    String getType();
    Long getCount();
}