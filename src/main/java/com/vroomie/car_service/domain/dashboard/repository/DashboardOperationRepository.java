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
    @Query(value = """
        SELECT
            (SELECT COUNT(*) FROM tbl_driving_log dl
             JOIN tbl_car c ON dl.car_id = c.id
             WHERE c.company_id = :companyId AND dl.log_status NOT IN ('COMPLETED', 'PREPARING')) as unwrittenDrivingLogs,
            (SELECT COUNT(*) FROM tbl_car c
             WHERE c.company_id = :companyId AND c.usage_type = 'ASSIGNED') as assignedVehicles,
            (SELECT COUNT(*) FROM tbl_reservation r
             JOIN tbl_car c ON r.car_id = c.id
             WHERE c.company_id = :companyId AND r.status = 'APPROVED') as rentedVehicles,
            (SELECT COUNT(*) FROM tbl_reservation r
             JOIN tbl_car c ON r.car_id = c.id
             WHERE c.company_id = :companyId AND r.ended_at < CURRENT_DATE AND r.status = 'APPROVED') as overdueVehicles
        """, nativeQuery = true)
    OperationalStatsProjection findOperationalStatsByCompany(@Param("companyId") Long companyId);
}