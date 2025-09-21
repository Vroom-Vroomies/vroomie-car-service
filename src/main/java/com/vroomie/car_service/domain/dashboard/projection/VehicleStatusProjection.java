package com.vroomie.car_service.domain.dashboard.projection;

/**
 * 차량 상태 프로젝션
 * - 대시보드에서 정상,정비,폐차와 같은 차량 상태 조회를 위한 프로젝션
 */
public interface VehicleStatusProjection {
    String getStatus();
    Long getCount();
}