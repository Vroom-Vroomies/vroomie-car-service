package com.vroomie.car_service.domain.dashboard.repository;

import com.vroomie.car_service.domain.fleet.drivinglog.entity.DrivingLogEntity;
import com.vroomie.car_service.domain.dashboard.projection.OperationalStatsProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DashboardOperationRepository extends JpaRepository<DrivingLogEntity, Long> {

    /**
     *
     * 운영 통계 쿼리 : 운행기록 미작성, 지급된 차량, 대여중 챠량, 반납 지연 차량을 조회합니다.
     * @param companyId 회사 아이디
     * @return OperationalStatsProjection 운영 통계 프로젝션
     */
    @Query("""
        SELECT
            (SELECT COUNT(dl) FROM DrivingLogEntity dl
             JOIN dl.carEntity c WHERE c.companyId = :companyId AND CAST(dl.logStatus AS string) NOT IN ('COMPLETED', 'PREPARING')) as unwrittenDrivingLogs,
            (SELECT COUNT(r) FROM ReservationEntity r
             JOIN r.car c WHERE c.companyId = :companyId AND r.status = 'ASSIGNED') as assignedVehicles,
            (SELECT COUNT(r) FROM ReservationEntity r
             JOIN r.car c WHERE c.companyId = :companyId AND r.status = 'RENTED') as rentedVehicles,
            (SELECT COUNT(r) FROM ReservationEntity r
             JOIN r.car c WHERE c.companyId = :companyId AND r.endedAt < CURRENT_DATE AND r.status = 'RENTED') as overdueVehicles
        """)
    OperationalStatsProjection findOperationalStatsByCompany(@Param("companyId") Long companyId);
}