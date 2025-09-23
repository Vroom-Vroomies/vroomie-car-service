package com.vroomie.car_service.domain.dashboard.projection;

/**
 * 차량 타입별 분포 프로젝션
 * - 대시보드에서 차량 종류별 분포 현황 조회를 위한 프로젝션
 */
public interface CarTypeDistributionProjection {
    String getType();
    Long getCount();
}