package com.vroomie.car_service.domain.dashboard.repository;

import com.vroomie.car_service.domain.dashboard.projection.AvailableFeeTypeProjection;
import com.vroomie.car_service.domain.dashboard.projection.MaintenanceBreakdownProjection;
import com.vroomie.car_service.domain.dashboard.projection.MonthlyCostProjection;
import com.vroomie.car_service.domain.dashboard.projection.VehicleMaintenanceProjection;
import com.vroomie.car_service.domain.fleet.drivinglog.entity.VariableCostEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 대시보드 비용 분석을 위한 리포지토리
 * 변동비용 데이터를 기반으로 월별, 차량별, 유지보수 비용 분석 데이터를 제공합니다.
 */
@Repository
public interface DashboardCostRepository extends JpaRepository<VariableCostEntity, Long> {

    /**
     * 변동 비용에서 월별 비용을 조회합니다.
     * 운행 기록의 변동 비용을 월별로 집계하여 반환합니다.
     *
     * @param companyId 회사 ID
     * @param startDateTime 조회 시작일시
     * @param endDateTime 조회 종료일시
     * @return 월별 변동 비용 리스트
     */
    @Query("""
        SELECT
            DATE_FORMAT(dl.createdAt, '%Y-%m') as month,
            vc.category as feeType,
            SUM(vc.cost) as amount,
            'variable_cost' as source
        FROM VariableCostEntity vc
        JOIN vc.drivingLogEntity dl
        JOIN dl.carEntity c
        WHERE c.companyId = :companyId
        AND dl.createdAt BETWEEN :startDateTime AND :endDateTime
        GROUP BY DATE_FORMAT(dl.createdAt, '%Y-%m'), vc.category
        ORDER BY DATE_FORMAT(dl.createdAt, '%Y-%m'), vc.category
        """)
    List<MonthlyCostProjection> findVariableCostsByMonth(
        @Param("companyId") Long companyId,
        @Param("startDateTime") LocalDateTime startDateTime,
        @Param("endDateTime") LocalDateTime endDateTime
    );

    /**
     * 차량별 변동 비용을 조회합니다.
     * 특정 기간 동안 각 차량의 변동 비용을 비용 유형별로 집계합니다.
     *
     * @param companyId 회사 ID
     * @param startDateTime 조회 시작일시
     * @param endDateTime 조회 종료일시
     * @param vehicleIds 조회할 차량 ID 리스트 (null이면 전체 차량)
     * @param pageable 페이징 정보
     * @return 차량별 변동 비용 리스트
     */
    @Query("""
        SELECT
            c.number as vehicleId,
            c.model as vehicleName,
            vc.category as feeType,
            SUM(vc.cost) as amount,
            DATE_FORMAT(dl.createdAt, '%Y-%m') as period,
            'variable_cost' as source
        FROM VariableCostEntity vc
        JOIN vc.drivingLogEntity dl
        JOIN dl.carEntity c
        WHERE c.companyId = :companyId
        AND dl.createdAt BETWEEN :startDateTime AND :endDateTime
        AND (:vehicleIds IS NULL OR c.number IN :vehicleIds)
        GROUP BY c.number, c.model, vc.category, DATE_FORMAT(dl.createdAt, '%Y-%m')
        ORDER BY c.number, vc.category
        """)
    List<VehicleMaintenanceProjection> findVehicleVariableCosts(
        @Param("companyId") Long companyId,
        @Param("startDateTime") LocalDateTime startDateTime,
        @Param("endDateTime") LocalDateTime endDateTime,
        @Param("vehicleIds") List<String> vehicleIds,
        Pageable pageable
    );

    /**
     * 유지보수 관련 비용 분류를 조회합니다.
     * 유지보수 카테고리의 변동 비용을 비용 유형별로 분석합니다.
     *
     * @param companyId 회사 ID
     * @param startDate 조회 시작일
     * @param endDate 조회 종료일
     * @return 유지보수 비용 분석 리스트
     */
    @Query("""
        SELECT
            vc.category as feeType,
            SUM(vc.cost) as amount,
            'variable_cost' as source
        FROM VariableCostEntity vc
        JOIN vc.drivingLogEntity dl
        JOIN dl.carEntity c
        WHERE c.companyId = :companyId
        AND dl.createdAt BETWEEN :startDate AND :endDate
        AND vc.category IN ('MAINTENANCE', 'REPAIR', 'INSPECTION')
        GROUP BY vc.category
        ORDER BY SUM(vc.cost) DESC
        """)
    List<MaintenanceBreakdownProjection> findMaintenanceBreakdown(
        @Param("companyId") Long companyId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    /**
     * 실제 사용 중인 비용 유형을 발견합니다.
     * 변동 비용 데이터에서 실제로 사용되고 있는 비용 유형들을 추출합니다.
     *
     * @param companyId 회사 ID
     * @return 사용 중인 비용 유형 리스트
     */
    @Query("""
        SELECT DISTINCT
            vc.category as feeType,
            'variable_cost' as source,
            SUM(vc.cost) as totalAmount
        FROM VariableCostEntity vc
        JOIN vc.drivingLogEntity dl
        JOIN dl.carEntity c
        WHERE c.companyId = :companyId
        GROUP BY vc.category
        HAVING SUM(vc.cost) > 0
        ORDER BY SUM(vc.cost) DESC
        """)
    List<AvailableFeeTypeProjection> findAvailableVariableFeeTypes(@Param("companyId") Long companyId);

    /**
     * 특정 기간의 유지보수 비용을 비용 유형별로 발생 건수와 함께 조회합니다.
     * 전월 대비 증감률 계산 및 평균 건당 비용 계산을 위한 데이터를 제공합니다.
     *
     * @param companyId 회사 ID
     * @param startDateTime 조회 시작일시
     * @param endDateTime 조회 종료일시
     * @return 유지보수 비용 상세 분석 리스트
     */
    @Query("""
        SELECT
            vc.category as feeType,
            SUM(vc.cost) as amount,
            COUNT(vc.id) as incidentCount,
            'variable_cost' as source
        FROM VariableCostEntity vc
        JOIN vc.drivingLogEntity dl
        JOIN dl.carEntity c
        WHERE c.companyId = :companyId
        AND dl.createdAt BETWEEN :startDateTime AND :endDateTime
        AND vc.category IN ('MAINTENANCE', 'REPAIR', 'INSPECTION')
        GROUP BY vc.category
        ORDER BY SUM(vc.cost) DESC
        """)
    List<MaintenanceBreakdownProjection> findMaintenanceBreakdownWithDetails(
        @Param("companyId") Long companyId,
        @Param("startDateTime") LocalDateTime startDateTime,
        @Param("endDateTime") LocalDateTime endDateTime
    );

    /**
     * 지난 N개월간 월별 유지보수 비용 추세를 조회합니다.
     * 추세 분석 및 예측을 위한 기간별 데이터를 제공합니다.
     *
     * @param companyId 회사 ID
     * @param startDateTime 조회 시작일시 (N개월 전)
     * @param endDateTime 조회 종료일시 (현재)
     * @return 월별 유지보수 비용 추세 리스트
     */
    @Query("""
        SELECT
            DATE_FORMAT(dl.createdAt, '%Y-%m') as month,
            SUM(vc.cost) as amount,
            'maintenance_trend' as source
        FROM VariableCostEntity vc
        JOIN vc.drivingLogEntity dl
        JOIN dl.carEntity c
        WHERE c.companyId = :companyId
        AND dl.createdAt BETWEEN :startDateTime AND :endDateTime
        AND vc.category IN ('MAINTENANCE', 'REPAIR', 'INSPECTION')
        GROUP BY DATE_FORMAT(dl.createdAt, '%Y-%m')
        ORDER BY DATE_FORMAT(dl.createdAt, '%Y-%m')
        """)
    List<MonthlyCostProjection> findMaintenanceTrendsByMonth(
        @Param("companyId") Long companyId,
        @Param("startDateTime") LocalDateTime startDateTime,
        @Param("endDateTime") LocalDateTime endDateTime
    );
}