package com.vroomie.car_service.domain.dashboard.projection;

/**
 * 운영 통계 프로젝션
 * - 대시보드에서 운행기록 미작성건, 지급된 차량 대수, 대여중 차량 대수, 반납지연차량 대수 조회를 위한 프로젝션
 */
public interface OperationalStatsProjection {
    Long getUnwrittenDrivingLogs(); // 운행 기록 미 작성건
    Long getAssignedVehicles(); // 지급된 차량
    Long getRentedVehicles(); // 대여중 차량
    Long getOverdueVehicles(); // 반납 지연 차량
}