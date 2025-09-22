package com.vroomie.car_service.domain.dashboard.repository;

import com.vroomie.car_service.domain.fleet.car.entity.CarEntity;
import com.vroomie.car_service.domain.dashboard.projection.CarTypeDistributionProjection;
import com.vroomie.car_service.domain.dashboard.projection.CarStatusProjection;
import com.vroomie.car_service.domain.fleet.car.enums.CarStatus;
import java.time.LocalDate;
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
    @Query(value = """
        SELECT '보험만료' as status, COUNT(*) as count
        FROM tbl_car c
        WHERE c.company_id = :companyId AND c.status = 'ACTIVE' AND c.insu_expiration < CURRENT_DATE

        UNION ALL

        SELECT '수리중' as status, COUNT(*) as count
        FROM tbl_car c
        WHERE c.company_id = :companyId AND c.status = 'ACTIVE'
          AND c.insu_expiration >= CURRENT_DATE
          AND EXISTS (SELECT 1 FROM tbl_repair r WHERE r.car_id = c.id AND r.status = 'IN_REPAIR')

        UNION ALL

        SELECT '점검중' as status, COUNT(*) as count
        FROM tbl_car c
        WHERE c.company_id = :companyId AND c.status = 'ACTIVE'
          AND c.insu_expiration >= CURRENT_DATE
          AND NOT EXISTS (SELECT 1 FROM tbl_repair r WHERE r.car_id = c.id AND r.status = 'IN_REPAIR')
          AND EXISTS (SELECT 1 FROM tbl_inspection i WHERE i.car_id = c.id AND i.final_result IS NULL)

        UNION ALL

        SELECT '정상' as status, COUNT(*) as count
        FROM tbl_car c
        WHERE c.company_id = :companyId AND c.status = 'ACTIVE'
          AND c.insu_expiration >= CURRENT_DATE
          AND NOT EXISTS (SELECT 1 FROM tbl_repair r WHERE r.car_id = c.id AND r.status = 'IN_REPAIR')
          AND NOT EXISTS (SELECT 1 FROM tbl_inspection i WHERE i.car_id = c.id AND i.final_result IS NULL)
        """, nativeQuery = true)
    List<CarStatusProjection> findCarStatusByCompany(@Param("companyId") Long companyId);

    /**
     * 회사가 보유한 차량 전체 수 조회
     * @param companyId 회사아이디
     * @return countByCompanyId
     */
    @Query("SELECT COUNT(c) FROM CarEntity c WHERE c.companyId = :companyId")
    Long countByCompanyId(@Param("companyId") Long companyId);


    // seoeungi 추가
    // 보험만료 카운트 쿼리
    // Before(<), After(>)
    // lessThan(<), greaterThan(>), lessThanOrEqual(>=), greaterThanOrEqual(<=)
    // Between
    /**
     * @param companyId Long : 회사 ID
     * @param status CarStatus : ACTIVE 또는 그 외
     * @param insuExpiration LocalDate : 만료일
     * @return Long
     *
     * @see CarStatus status
     */
    Long countByCompanyIdAndStatusAndInsuExpirationBefore(Long companyId, CarStatus status, LocalDate insuExpiration);
}