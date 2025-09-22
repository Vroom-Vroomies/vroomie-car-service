package com.vroomie.car_service.domain.dashboard.repository;

import com.vroomie.car_service.domain.fleet.car.entity.CarEntity;
import com.vroomie.car_service.domain.dashboard.projection.CarTypeDistributionProjection;
import com.vroomie.car_service.domain.dashboard.projection.CarStatusProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DashboardCarRepository extends JpaRepository<CarEntity, Long> {

    /**
     * 회사별 차량 타입별 분포 조회
     * @param companyId 회사 아이디
     * @return List<CarTypeDistributionProjection>
     */
    @Query("""
        SELECT c.type as type, COUNT(c) as count
        FROM CarEntity c
        WHERE c.companyId = :companyId
        GROUP BY c.type
        """)
    List<CarTypeDistributionProjection> findCarTypeDistributionByCompany(@Param("companyId") Long companyId);

    /**
     * 회사별 차량 상태 분포 조회 (동적 상태 계산)
     * - 보험만료: 보험 만료일이 현재 날짜보다 이전
     * - 수리중: 진행 중인 수리가 있는 차량
     * - 점검중: 진행 중인 점검이 있는 차량
     * - 정상: 위 조건에 해당하지 않는 ACTIVE 상태 차량
     * @param companyId 회사 아이디
     * @return List<CarStatusProjection>
     */
    @Query("""
        SELECT cs.status as status, COUNT(cs.status) as count
        FROM (
            SELECT c.id,
                CASE
                    WHEN c.insuExpiration < CURRENT_DATE THEN '보험만료'
                    WHEN EXISTS (SELECT 1 FROM RepairEntity r WHERE r.car.id = c.id AND CAST(r.status AS string) IN ('PENDING', 'IN_PROGRESS')) THEN '수리중'
                    WHEN EXISTS (SELECT 1 FROM InspectionEntity i WHERE i.car.id = c.id AND i.finalResult IS NULL) THEN '점검중'
                    ELSE '정상'
                END as status
            FROM CarEntity c
            WHERE c.companyId = :companyId AND c.status = 'ACTIVE'
        ) cs
        GROUP BY cs.status
        """)
    List<CarStatusProjection> findCarStatusByCompany(@Param("companyId") Long companyId);

    /**
     * 회사가 보유한 차량 전체 수 조회
     * @param companyId 회사아이디
     * @return countByCompanyId
     */
    @Query("SELECT COUNT(c) FROM CarEntity c WHERE c.companyId = :companyId")
    Long countByCompanyId(@Param("companyId") Long companyId);
}