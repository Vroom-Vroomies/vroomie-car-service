package com.vroomie.car_service.domain.dashboard.repository;

import com.vroomie.car_service.domain.dashboard.entity.TcoAnalysisEntity;
import com.vroomie.car_service.domain.dashboard.projection.CostSavingOpportunityProjection;
import com.vroomie.car_service.domain.dashboard.projection.TcoProjection;
import com.vroomie.car_service.domain.dashboard.projection.TcoRecommendationProjection;
import com.vroomie.car_service.domain.dashboard.projection.TcoAnalysisProjection;
import com.vroomie.car_service.domain.dashboard.projection.CostDistributionProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * TCO 분석을 위한 리포지토리
 * 총 소유비용 분석, 권장사항, 비용 절감 기회 데이터를 제공합니다.
 */
@Repository
public interface TcoAnalysisRepository extends JpaRepository<TcoAnalysisEntity, Long> {

    /**
     * 회사별 TCO 분석 데이터를 조회합니다.
     * 계약, 변동비용 데이터를 기반으로 차량별 총 소유비용을 계산합니다.
     *
     * @param companyId 회사 ID
     * @param year 분석 연도 (0이면 모든 연도)
     * @param month 분석 월 (0이면 모든 월)
     * @return TCO 분석 결과 리스트
     */
    @Query("""
        SELECT
            c.number as vehicleId,
            c.model as vehicleName,
            COALESCE(cc.monthlyFee * 12, 0) as acquisitionCost,
            COALESCE(SUM(CASE WHEN vc.category IN ('FUEL_COST', 'PARKING_COST', 'TOLL_FEE') THEN vc.cost ELSE 0 END), 0) as operationCost,
            COALESCE(SUM(CASE WHEN vc.category IN ('CAR_WASH_COST', 'FINE') THEN vc.cost ELSE 0 END), 0) as maintenanceCost,
            0 as disposalCost,
            (COALESCE(cc.monthlyFee * 12, 0) +
             COALESCE(SUM(CASE WHEN vc.category IN ('FUEL_COST', 'PARKING_COST', 'TOLL_FEE') THEN vc.cost ELSE 0 END), 0) +
             COALESCE(SUM(CASE WHEN vc.category IN ('CAR_WASH_COST', 'FINE') THEN vc.cost ELSE 0 END), 0)) as totalCost,
            :year as year,
            :month as month
        FROM CarEntity c
        LEFT JOIN CarContract cc ON c.id = cc.car.id AND cc.contractStatus IN ('NEW', 'RENEWED')
        LEFT JOIN DrivingLogEntity dl ON c.id = dl.carEntity.id
            AND (:year = 0 OR YEAR(dl.createdAt) = :year)
            AND (:month = 0 OR MONTH(dl.createdAt) = :month)
        LEFT JOIN VariableCostEntity vc ON dl.id = vc.drivingLogEntity.id
        WHERE c.companyId = :companyId
        GROUP BY c.id, c.number, c.model, cc.monthlyFee
        ORDER BY (COALESCE(cc.monthlyFee * 12, 0) +
                 COALESCE(SUM(CASE WHEN vc.category IN ('FUEL_COST', 'PARKING_COST', 'TOLL_FEE') THEN vc.cost ELSE 0 END), 0) +
                 COALESCE(SUM(CASE WHEN vc.category IN ('CAR_WASH_COST', 'FINE') THEN vc.cost ELSE 0 END), 0)) DESC
        """)
    List<TcoProjection> findTcoAnalysisByCompany(
        @Param("companyId") Long companyId,
        @Param("year") Integer year,
        @Param("month") Integer month
    );

    /**
     * TCO 기반 차량 관리 권장사항을 조회합니다.
     * 비효율성 점수와 차량 연식을 기반으로 처분/교체/정비 권장사항을 제공합니다.
     *
     * @param companyId 회사 ID
     * @param year 분석 연도 (0이면 모든 연도)
     * @param month 분석 월 (0이면 모든 월)
     * @return 권장사항 리스트
     */
    @Query("""
        SELECT
            c.number as vehicleId,
            c.model as vehicleName,
            CASE
                WHEN ve.inefficiencyScore > 80 THEN 'disposal'
                WHEN YEAR(CURRENT_DATE) - c.year > 7 THEN 'replacement'
                ELSE 'maintenance'
            END as recommendationType,
            CASE
                WHEN ve.inefficiencyScore > 80 THEN '높은 유지비용으로 인한 비효율성'
                WHEN YEAR(CURRENT_DATE) - c.year > 7 THEN '차량 연식으로 인한 교체 시기 도래'
                ELSE '정기 정비를 통한 효율성 개선 필요'
            END as reason,
            CASE
                WHEN ve.inefficiencyScore > 80 THEN 'high'
                WHEN YEAR(CURRENT_DATE) - c.year > 7 THEN 'medium'
                ELSE 'low'
            END as priority,
            COALESCE(ve.totalCost * 0.15, 0) as estimatedSaving,
            COALESCE(ve.totalCost, 0) as currentCost,
            (YEAR(CURRENT_DATE) - c.year) as vehicleAge,
            COALESCE(ve.inefficiencyScore, 0) as efficiencyScore
        FROM CarEntity c
        LEFT JOIN VehicleEfficiencyEntity ve ON c.number = ve.vehicleId AND ve.companyId = c.companyId
            AND (:year = 0 OR ve.year = :year)
            AND (:month = 0 OR ve.month = :month)
        WHERE c.companyId = :companyId
        AND (ve.inefficiencyScore > 60 OR YEAR(CURRENT_DATE) - c.year > 5)
        ORDER BY COALESCE(ve.inefficiencyScore, 0) DESC, (YEAR(CURRENT_DATE) - c.year) DESC
        """)
    List<TcoRecommendationProjection> findTcoRecommendations(
        @Param("companyId") Long companyId,
        @Param("year") Integer year,
        @Param("month") Integer month
    );

    /**
     * 비용 절감 기회를 분석합니다.
     * 카테고리별 비용 현황을 분석하여 절감 가능한 영역을 식별합니다.
     *
     * @param companyId 회사 ID
     * @param startDate 분석 시작일
     * @param endDate 분석 종료일
     * @return 비용 절감 기회 리스트
     */
    @Query("""
        SELECT
            'OPERATIONAL' as category,
            15.0 as potentialSaving,
            'percentage' as unit,
            '운영비 최적화를 통한 비용 절감 가능' as description,
            SUM(vc.cost) * 0.15 as estimatedAmount,
            SUM(vc.cost) as currentAmount
        FROM VariableCostEntity vc
        JOIN vc.drivingLogEntity dl
        JOIN dl.carEntity c
        WHERE c.companyId = :companyId
        AND dl.createdAt BETWEEN :startDate AND :endDate
        AND vc.category IN ('FUEL_COST', 'PARKING_COST', 'TOLL_FEE')
        AND vc.cost > 0
        GROUP BY c.companyId
        HAVING SUM(vc.cost) > 0

        UNION ALL

        SELECT
            'MAINTENANCE' as category,
            12.0 as potentialSaving,
            'percentage' as unit,
            '정비 계약 재검토를 통한 비용 절감 가능' as description,
            SUM(vc.cost) * 0.12 as estimatedAmount,
            SUM(vc.cost) as currentAmount
        FROM VariableCostEntity vc
        JOIN vc.drivingLogEntity dl
        JOIN dl.carEntity c
        WHERE c.companyId = :companyId
        AND dl.createdAt BETWEEN :startDate AND :endDate
        AND vc.category IN ('CAR_WASH_COST', 'FINE')
        AND vc.cost > 0
        GROUP BY c.companyId
        HAVING SUM(vc.cost) > 0
        """)
    List<CostSavingOpportunityProjection> findCostSavingOpportunities(
        @Param("companyId") Long companyId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * 특정 기간의 TCO 분석 결과를 저장된 엔티티에서 조회합니다.
     * 배치 작업으로 미리 계산된 TCO 데이터를 조회할 때 사용합니다.
     *
     * @param companyId 회사 ID
     * @param period 분석 기간
     * @param year 분석 연도
     * @param month 분석 월 (0이면 모든 월)
     * @return 저장된 TCO 분석 결과
     */
    @Query("""
        SELECT tco FROM TcoAnalysisEntity tco
        WHERE tco.companyId = :companyId
        AND tco.period = :period
        AND tco.year = :year
        AND (:month = 0 OR tco.month = :month)
        ORDER BY tco.totalCost DESC
        """)
    List<TcoAnalysisEntity> findStoredTcoAnalysis(
        @Param("companyId") Long companyId,
        @Param("period") TcoAnalysisEntity.AnalysisPeriod period,
        @Param("year") Integer year,
        @Param("month") Integer month
    );

    /**
     * 회사별 TCO 분석 데이터를 페이지네이션으로 조회합니다.
     *
     * @param companyId 회사 ID
     * @param year 분석 연도 (0이면 모든 연도)
     * @param month 분석 월 (0이면 모든 월)
     * @param pageable 페이지네이션 정보
     * @return TCO 분석 결과 페이지
     */
    @Query("""
        SELECT
            c.number as vehicleId,
            c.model as vehicleName,
            COALESCE(cc.monthlyFee * 12, 0) as acquisitionCost,
            COALESCE(SUM(CASE WHEN vc.category IN ('FUEL_COST', 'PARKING_COST', 'TOLL_FEE') THEN vc.cost ELSE 0 END), 0) as operationCost,
            COALESCE(SUM(CASE WHEN vc.category IN ('CAR_WASH_COST', 'FINE') THEN vc.cost ELSE 0 END), 0) as maintenanceCost,
            0 as disposalCost,
            (COALESCE(cc.monthlyFee * 12, 0) +
             COALESCE(SUM(CASE WHEN vc.category IN ('FUEL_COST', 'PARKING_COST', 'TOLL_FEE') THEN vc.cost ELSE 0 END), 0) +
             COALESCE(SUM(CASE WHEN vc.category IN ('CAR_WASH_COST', 'FINE') THEN vc.cost ELSE 0 END), 0)) as totalCost,
            :year as year,
            :month as month
        FROM CarEntity c
        LEFT JOIN CarContract cc ON c.id = cc.car.id AND cc.contractStatus IN ('NEW', 'RENEWED')
        LEFT JOIN DrivingLogEntity dl ON c.id = dl.carEntity.id
            AND (:year = 0 OR YEAR(dl.createdAt) = :year)
            AND (:month = 0 OR MONTH(dl.createdAt) = :month)
        LEFT JOIN VariableCostEntity vc ON dl.id = vc.drivingLogEntity.id
        WHERE c.companyId = :companyId
        GROUP BY c.id, c.number, c.model, cc.monthlyFee
        ORDER BY (COALESCE(cc.monthlyFee * 12, 0) +
                 COALESCE(SUM(CASE WHEN vc.category IN ('FUEL_COST', 'PARKING_COST', 'TOLL_FEE') THEN vc.cost ELSE 0 END), 0) +
                 COALESCE(SUM(CASE WHEN vc.category IN ('CAR_WASH_COST', 'FINE') THEN vc.cost ELSE 0 END), 0)) DESC
        """)
    Page<TcoProjection> findTcoAnalysisByCompanyWithPagination(
        @Param("companyId") Long companyId,
        @Param("year") Integer year,
        @Param("month") Integer month,
        Pageable pageable
    );

    /**
     * TCO 권장사항을 페이지네이션으로 조회합니다.
     *
     * @param companyId 회사 ID
     * @param year 분석 연도 (0이면 모든 연도)
     * @param month 분석 월 (0이면 모든 월)
     * @param pageable 페이지네이션 정보
     * @return 권장사항 페이지
     */
    @Query("""
        SELECT
            c.number as vehicleId,
            c.model as vehicleName,
            CASE
                WHEN ve.inefficiencyScore > 80 THEN 'disposal'
                WHEN YEAR(CURRENT_DATE) - c.year > 7 THEN 'replacement'
                ELSE 'maintenance'
            END as recommendationType,
            CASE
                WHEN ve.inefficiencyScore > 80 THEN '높은 유지비용으로 인한 비효율성'
                WHEN YEAR(CURRENT_DATE) - c.year > 7 THEN '차량 연식으로 인한 교체 시기 도래'
                ELSE '정기 정비를 통한 효율성 개선 필요'
            END as reason,
            CASE
                WHEN ve.inefficiencyScore > 80 THEN 'high'
                WHEN YEAR(CURRENT_DATE) - c.year > 7 THEN 'medium'
                ELSE 'low'
            END as priority,
            COALESCE(ve.totalCost * 0.15, 0) as estimatedSaving,
            COALESCE(ve.totalCost, 0) as currentCost,
            (YEAR(CURRENT_DATE) - c.year) as vehicleAge,
            COALESCE(ve.inefficiencyScore, 0) as efficiencyScore
        FROM CarEntity c
        LEFT JOIN VehicleEfficiencyEntity ve ON c.number = ve.vehicleId AND ve.companyId = c.companyId
            AND (:year = 0 OR ve.year = :year)
            AND (:month = 0 OR ve.month = :month)
        WHERE c.companyId = :companyId
        AND (ve.inefficiencyScore > 60 OR YEAR(CURRENT_DATE) - c.year > 5)
        ORDER BY COALESCE(ve.inefficiencyScore, 0) DESC, (YEAR(CURRENT_DATE) - c.year) DESC
        """)
    Page<TcoRecommendationProjection> findTcoRecommendationsWithPagination(
        @Param("companyId") Long companyId,
        @Param("year") Integer year,
        @Param("month") Integer month,
        Pageable pageable
    );

    /**
     * 회사별 TCO 요약 통계를 조회합니다.
     *
     * @param companyId 회사 ID
     * @param year 분석 연도 (0이면 모든 연도)
     * @param month 분석 월 (0이면 모든 월)
     * @return TCO 요약 통계 배열 [총 차량 수, 평균 TCO, 최고 TCO, 최저 TCO, 총 TCO]
     */
    @Query("""
        SELECT
            COUNT(DISTINCT c.id),
            AVG(COALESCE(cc.monthlyFee * 12, 0) +
                COALESCE(SUM(CASE WHEN vc.category IN ('FUEL_COST', 'PARKING_COST', 'TOLL_FEE') THEN vc.cost ELSE 0 END), 0) +
                COALESCE(SUM(CASE WHEN vc.category IN ('CAR_WASH_COST', 'FINE') THEN vc.cost ELSE 0 END), 0)),
            MAX(COALESCE(cc.monthlyFee * 12, 0) +
                COALESCE(SUM(CASE WHEN vc.category IN ('FUEL_COST', 'PARKING_COST', 'TOLL_FEE') THEN vc.cost ELSE 0 END), 0) +
                COALESCE(SUM(CASE WHEN vc.category IN ('CAR_WASH_COST', 'FINE') THEN vc.cost ELSE 0 END), 0)),
            MIN(COALESCE(cc.monthlyFee * 12, 0) +
                COALESCE(SUM(CASE WHEN vc.category IN ('FUEL_COST', 'PARKING_COST', 'TOLL_FEE') THEN vc.cost ELSE 0 END), 0) +
                COALESCE(SUM(CASE WHEN vc.category IN ('CAR_WASH_COST', 'FINE') THEN vc.cost ELSE 0 END), 0)),
            SUM(COALESCE(cc.monthlyFee * 12, 0) +
                COALESCE(SUM(CASE WHEN vc.category IN ('FUEL_COST', 'PARKING_COST', 'TOLL_FEE') THEN vc.cost ELSE 0 END), 0) +
                COALESCE(SUM(CASE WHEN vc.category IN ('CAR_WASH_COST', 'FINE') THEN vc.cost ELSE 0 END), 0))
        FROM CarEntity c
        LEFT JOIN CarContract cc ON c.id = cc.car.id AND cc.contractStatus IN ('NEW', 'RENEWED')
        LEFT JOIN DrivingLogEntity dl ON c.id = dl.carEntity.id
            AND (:year = 0 OR YEAR(dl.createdAt) = :year)
            AND (:month = 0 OR MONTH(dl.createdAt) = :month)
        LEFT JOIN VariableCostEntity vc ON dl.id = vc.drivingLogEntity.id
        WHERE c.companyId = :companyId
        """)
    Object[] findTcoSummaryStats(
        @Param("companyId") Long companyId,
        @Param("year") Integer year,
        @Param("month") Integer month
    );

    /**
     * 특정 차량의 TCO 상세 분석을 조회합니다.
     *
     * @param companyId 회사 ID
     * @param vehicleId 차량 ID
     * @param year 분석 연도 (0이면 모든 연도)
     * @param month 분석 월 (0이면 모든 월)
     * @return 차량별 TCO 상세 분석
     */
    @Query("""
        SELECT
            c.number as vehicleId,
            c.model as vehicleName,
            COALESCE(cc.monthlyFee * 12, 0) as acquisitionCost,
            COALESCE(SUM(CASE WHEN vc.category IN ('FUEL_COST', 'PARKING_COST', 'TOLL_FEE') THEN vc.cost ELSE 0 END), 0) as operationCost,
            COALESCE(SUM(CASE WHEN vc.category IN ('CAR_WASH_COST', 'FINE') THEN vc.cost ELSE 0 END), 0) as maintenanceCost,
            0 as disposalCost,
            (COALESCE(cc.monthlyFee * 12, 0) +
             COALESCE(SUM(CASE WHEN vc.category IN ('FUEL_COST', 'PARKING_COST', 'TOLL_FEE') THEN vc.cost ELSE 0 END), 0) +
             COALESCE(SUM(CASE WHEN vc.category IN ('CAR_WASH_COST', 'FINE') THEN vc.cost ELSE 0 END), 0)) as totalCost,
            :year as year,
            :month as month
        FROM CarEntity c
        LEFT JOIN CarContract cc ON c.id = cc.car.id AND cc.contractStatus IN ('NEW', 'RENEWED')
        LEFT JOIN DrivingLogEntity dl ON c.id = dl.carEntity.id
            AND (:year = 0 OR YEAR(dl.createdAt) = :year)
            AND (:month = 0 OR MONTH(dl.createdAt) = :month)
        LEFT JOIN VariableCostEntity vc ON dl.id = vc.drivingLogEntity.id
        WHERE c.companyId = :companyId
        AND c.number = :vehicleId
        GROUP BY c.id, c.number, c.model, cc.monthlyFee
        """)
    TcoProjection findVehicleTcoDetail(
        @Param("companyId") Long companyId,
        @Param("vehicleId") String vehicleId,
        @Param("year") Integer year,
        @Param("month") Integer month
    );

    /**
     * 서비스에서 사용하는 TCO 분석 데이터를 필터와 함께 조회합니다.
     */
    @Query("""
        SELECT
            c.number as vehicleId,
            c.model as vehicleName,
            c.type as vehicleType,
            cc.contractType as contractType,
            (COALESCE(cc.monthlyFee * 12, 0) +
             COALESCE(SUM(CASE WHEN vc.category IN ('FUEL_COST', 'PARKING_COST', 'TOLL_FEE') THEN vc.cost ELSE 0 END), 0) +
             COALESCE(SUM(CASE WHEN vc.category IN ('CAR_WASH_COST', 'FINE') THEN vc.cost ELSE 0 END), 0)) as totalCost,
            COALESCE(cc.monthlyFee * 12, 0) as contractCost,
            COALESCE(SUM(CASE WHEN vc.category IN ('FUEL_COST', 'PARKING_COST', 'TOLL_FEE') THEN vc.cost ELSE 0 END), 0) as operationalCost,
            COALESCE(SUM(CASE WHEN vc.category IN ('CAR_WASH_COST', 'FINE') THEN vc.cost ELSE 0 END), 0) as maintenanceCost,
            COALESCE(SUM(COALESCE(dl.odometerDistance, dl.gpsDistance)), 0) as mileage,
            COALESCE(COUNT(dl.id), 0) as usageDays
        FROM CarEntity c
        LEFT JOIN CarContract cc ON c.id = cc.car.id AND cc.contractStatus IN ('NEW', 'RENEWED')
        LEFT JOIN DrivingLogEntity dl ON c.id = dl.carEntity.id
            AND dl.createdAt BETWEEN :startDate AND :endDate
        LEFT JOIN VariableCostEntity vc ON dl.id = vc.drivingLogEntity.id
        WHERE c.companyId = :companyId
        AND (:minTotalCost IS NULL OR (COALESCE(cc.monthlyFee * 12, 0) +
             COALESCE(SUM(CASE WHEN vc.category IN ('FUEL_COST', 'PARKING_COST', 'TOLL_FEE') THEN vc.cost ELSE 0 END), 0) +
             COALESCE(SUM(CASE WHEN vc.category IN ('CAR_WASH_COST', 'FINE') THEN vc.cost ELSE 0 END), 0)) >= :minTotalCost)
        AND (:maxTotalCost IS NULL OR (COALESCE(cc.monthlyFee * 12, 0) +
             COALESCE(SUM(CASE WHEN vc.category IN ('FUEL_COST', 'PARKING_COST', 'TOLL_FEE') THEN vc.cost ELSE 0 END), 0) +
             COALESCE(SUM(CASE WHEN vc.category IN ('CAR_WASH_COST', 'FINE') THEN vc.cost ELSE 0 END), 0)) <= :maxTotalCost)
        AND (:contractTypes IS NULL OR cc.contractType IN :contractTypes)
        AND (:vehicleTypes IS NULL OR c.type IN :vehicleTypes)
        GROUP BY c.id, c.number, c.model, c.type, cc.contractType, cc.monthlyFee
        ORDER BY (COALESCE(cc.monthlyFee * 12, 0) +
                 COALESCE(SUM(CASE WHEN vc.category IN ('FUEL_COST', 'PARKING_COST', 'TOLL_FEE') THEN vc.cost ELSE 0 END), 0) +
                 COALESCE(SUM(CASE WHEN vc.category IN ('CAR_WASH_COST', 'FINE') THEN vc.cost ELSE 0 END), 0)) DESC
        """)
    Page<TcoAnalysisProjection> findTcoAnalysisWithFilters(
        @Param("companyId") Long companyId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("minTotalCost") BigDecimal minTotalCost,
        @Param("maxTotalCost") BigDecimal maxTotalCost,
        @Param("contractTypes") List<String> contractTypes,
        @Param("vehicleTypes") List<String> vehicleTypes,
        Pageable pageable
    );


    /**
     * 비용 분포 데이터를 조회합니다.
     */
    @Query("""
        SELECT
            'CONTRACT' as category,
            SUM(COALESCE(cc.monthlyFee * 12, 0)) as amount,
            COUNT(DISTINCT c.id) as vehicleCount
        FROM CarEntity c
        LEFT JOIN CarContract cc ON c.id = cc.car.id AND cc.contractStatus IN ('NEW', 'RENEWED')
        LEFT JOIN DrivingLogEntity dl ON c.id = dl.carEntity.id
            AND dl.createdAt BETWEEN :startDate AND :endDate
        WHERE c.companyId = :companyId

        UNION ALL

        SELECT
            'OPERATIONAL' as category,
            SUM(COALESCE(vc.cost, 0)) as amount,
            COUNT(DISTINCT c.id) as vehicleCount
        FROM CarEntity c
        LEFT JOIN DrivingLogEntity dl ON c.id = dl.carEntity.id
            AND dl.createdAt BETWEEN :startDate AND :endDate
        LEFT JOIN VariableCostEntity vc ON dl.id = vc.drivingLogEntity.id
            AND vc.category IN ('FUEL_COST', 'PARKING_COST', 'TOLL_FEE')
        WHERE c.companyId = :companyId

        UNION ALL

        SELECT
            'MAINTENANCE' as category,
            SUM(COALESCE(vc.cost, 0)) as amount,
            COUNT(DISTINCT c.id) as vehicleCount
        FROM CarEntity c
        LEFT JOIN DrivingLogEntity dl ON c.id = dl.carEntity.id
            AND dl.createdAt BETWEEN :startDate AND :endDate
        LEFT JOIN VariableCostEntity vc ON dl.id = vc.drivingLogEntity.id
            AND vc.category IN ('CAR_WASH_COST', 'FINE')
        WHERE c.companyId = :companyId
        """)
    List<CostDistributionProjection> findCostDistribution(
        @Param("companyId") Long companyId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
}