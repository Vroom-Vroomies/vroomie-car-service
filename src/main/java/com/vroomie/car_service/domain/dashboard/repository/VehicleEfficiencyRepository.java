package com.vroomie.car_service.domain.dashboard.repository;

import com.vroomie.car_service.domain.dashboard.entity.VehicleEfficiencyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 차량 효율성 메트릭을 위한 리포지토리
 * 차량별 효율성 지표 조회, 분석, 통계 기능을 제공합니다.
 */
@Repository
public interface VehicleEfficiencyRepository extends JpaRepository<VehicleEfficiencyEntity, Long> {

    /**
     * 회사별 차량 효율성 데이터를 조회합니다.
     *
     * @param companyId 회사 ID
     * @param period 분석 기간
     * @param year 분석 연도
     * @param month 분석 월 (0이면 모든 월)
     * @param pageable 페이지네이션 정보
     * @return 차량 효율성 데이터 페이지
     */
    @Query("""
        SELECT ve FROM VehicleEfficiencyEntity ve
        WHERE ve.companyId = :companyId
        AND ve.period = :period
        AND ve.year = :year
        AND (:month = 0 OR ve.month = :month)
        ORDER BY ve.inefficiencyScore DESC, ve.totalCost DESC
        """)
    Page<VehicleEfficiencyEntity> findByCompanyAndPeriod(
        @Param("companyId") Long companyId,
        @Param("period") VehicleEfficiencyEntity.AnalysisPeriod period,
        @Param("year") Integer year,
        @Param("month") Integer month,
        Pageable pageable
    );

    /**
     * 특정 차량의 효율성 데이터를 조회합니다.
     *
     * @param companyId 회사 ID
     * @param vehicleId 차량 ID
     * @param period 분석 기간
     * @param year 분석 연도
     * @param month 분석 월
     * @return 차량 효율성 데이터
     */
    Optional<VehicleEfficiencyEntity> findByCompanyIdAndVehicleIdAndPeriodAndYearAndMonth(
        Long companyId,
        String vehicleId,
        VehicleEfficiencyEntity.AnalysisPeriod period,
        Integer year,
        Integer month
    );

    /**
     * 비효율성 점수가 임계값 이상인 차량들을 조회합니다.
     *
     * @param companyId 회사 ID
     * @param inefficiencyThreshold 비효율성 점수 임계값
     * @param year 분석 연도
     * @param month 분석 월 (0이면 모든 월)
     * @return 비효율적인 차량 리스트
     */
    @Query("""
        SELECT ve FROM VehicleEfficiencyEntity ve
        WHERE ve.companyId = :companyId
        AND ve.inefficiencyScore >= :inefficiencyThreshold
        AND ve.year = :year
        AND (:month = 0 OR ve.month = :month)
        ORDER BY ve.inefficiencyScore DESC
        """)
    List<VehicleEfficiencyEntity> findInefficiencyVehicles(
        @Param("companyId") Long companyId,
        @Param("inefficiencyThreshold") BigDecimal inefficiencyThreshold,
        @Param("year") Integer year,
        @Param("month") Integer month
    );

    /**
     * 회사의 평균 효율성 지표를 계산합니다.
     *
     * @param companyId 회사 ID
     * @param period 분석 기간
     * @param year 분석 연도
     * @param month 분석 월 (0이면 모든 월)
     * @return 평균 효율성 점수
     */
    @Query("""
        SELECT AVG(ve.inefficiencyScore)
        FROM VehicleEfficiencyEntity ve
        WHERE ve.companyId = :companyId
        AND ve.period = :period
        AND ve.year = :year
        AND (:month = 0 OR ve.month = :month)
        AND ve.inefficiencyScore > 0
        """)
    Optional<BigDecimal> findAverageInefficiencyScore(
        @Param("companyId") Long companyId,
        @Param("period") VehicleEfficiencyEntity.AnalysisPeriod period,
        @Param("year") Integer year,
        @Param("month") Integer month
    );

    /**
     * 효율성 개선이 필요한 차량 수를 조회합니다.
     *
     * @param companyId 회사 ID
     * @param inefficiencyThreshold 비효율성 점수 임계값
     * @param year 분석 연도
     * @param month 분석 월 (0이면 모든 월)
     * @return 개선 대상 차량 수
     */
    @Query("""
        SELECT COUNT(ve)
        FROM VehicleEfficiencyEntity ve
        WHERE ve.companyId = :companyId
        AND ve.inefficiencyScore >= :inefficiencyThreshold
        AND ve.year = :year
        AND (:month = 0 OR ve.month = :month)
        """)
    long countVehiclesNeedingImprovement(
        @Param("companyId") Long companyId,
        @Param("inefficiencyThreshold") BigDecimal inefficiencyThreshold,
        @Param("year") Integer year,
        @Param("month") Integer month
    );

    /**
     * 효율성이 가장 좋은 상위 N개 차량을 조회합니다.
     *
     * @param companyId 회사 ID
     * @param period 분석 기간
     * @param year 분석 연도
     * @param month 분석 월 (0이면 모든 월)
     * @param pageable 페이지네이션 정보
     * @return 효율성 상위 차량 리스트
     */
    @Query("""
        SELECT ve FROM VehicleEfficiencyEntity ve
        WHERE ve.companyId = :companyId
        AND ve.period = :period
        AND ve.year = :year
        AND (:month = 0 OR ve.month = :month)
        AND ve.inefficiencyScore > 0
        ORDER BY ve.inefficiencyScore ASC, ve.costPerKm ASC
        """)
    List<VehicleEfficiencyEntity> findTopEfficientVehicles(
        @Param("companyId") Long companyId,
        @Param("period") VehicleEfficiencyEntity.AnalysisPeriod period,
        @Param("year") Integer year,
        @Param("month") Integer month,
        Pageable pageable
    );

    /**
     * 특정 기간의 차량별 연비 효율성 순위를 조회합니다.
     *
     * @param companyId 회사 ID
     * @param year 분석 연도
     * @param month 분석 월 (0이면 모든 월)
     * @return 연비 효율성 순위 리스트
     */
    @Query("""
        SELECT ve FROM VehicleEfficiencyEntity ve
        WHERE ve.companyId = :companyId
        AND ve.year = :year
        AND (:month = 0 OR ve.month = :month)
        AND ve.fuelEfficiency > 0
        ORDER BY ve.fuelEfficiency DESC
        """)
    List<VehicleEfficiencyEntity> findByFuelEfficiencyRanking(
        @Param("companyId") Long companyId,
        @Param("year") Integer year,
        @Param("month") Integer month
    );

    /**
     * 차량 효율성 트렌드 분석을 위한 이전 기간 대비 데이터를 조회합니다.
     *
     * @param companyId 회사 ID
     * @param vehicleId 차량 ID
     * @param currentYear 현재 연도
     * @param currentMonth 현재 월 (0이면 모든 월)
     * @param previousYear 이전 연도
     * @param previousMonth 이전 월 (0이면 모든 월)
     * @return 현재와 이전 기간의 효율성 데이터
     */
    @Query("""
        SELECT ve FROM VehicleEfficiencyEntity ve
        WHERE ve.companyId = :companyId
        AND ve.vehicleId = :vehicleId
        AND ((ve.year = :currentYear AND (:currentMonth = 0 OR ve.month = :currentMonth))
             OR (ve.year = :previousYear AND (:previousMonth = 0 OR ve.month = :previousMonth)))
        ORDER BY ve.year DESC, ve.month DESC
        """)
    List<VehicleEfficiencyEntity> findEfficiencyTrend(
        @Param("companyId") Long companyId,
        @Param("vehicleId") String vehicleId,
        @Param("currentYear") Integer currentYear,
        @Param("currentMonth") Integer currentMonth,
        @Param("previousYear") Integer previousYear,
        @Param("previousMonth") Integer previousMonth
    );

    /**
     * 회사별 월별 효율성 통계를 조회합니다.
     *
     * @param companyId 회사 ID
     * @param year 분석 연도
     * @return 월별 효율성 통계
     */
    @Query("""
        SELECT
            ve.month,
            AVG(ve.inefficiencyScore),
            AVG(ve.costPerKm),
            AVG(ve.fuelEfficiency),
            COUNT(ve)
        FROM VehicleEfficiencyEntity ve
        WHERE ve.companyId = :companyId
        AND ve.year = :year
        AND ve.period = 'MONTH'
        AND ve.month > 0
        GROUP BY ve.month
        ORDER BY ve.month
        """)
    List<Object[]> findMonthlyEfficiencyStats(
        @Param("companyId") Long companyId,
        @Param("year") Integer year
    );

    /**
     * 특정 기간 내에서 데이터가 존재하는지 확인합니다.
     *
     * @param companyId 회사 ID
     * @param period 분석 기간
     * @param year 분석 연도
     * @param month 분석 월
     * @return 데이터 존재 여부
     */
    boolean existsByCompanyIdAndPeriodAndYearAndMonth(
        Long companyId,
        VehicleEfficiencyEntity.AnalysisPeriod period,
        Integer year,
        Integer month
    );

    /**
     * 특정 차량의 최신 효율성 데이터를 조회합니다.
     *
     * @param companyId 회사 ID
     * @param vehicleId 차량 ID
     * @param pageable 페이지네이션 정보 (첫 번째 결과만 가져오기 위해)
     * @return 최신 효율성 데이터 리스트
     */
    @Query("""
        SELECT ve FROM VehicleEfficiencyEntity ve
        WHERE ve.companyId = :companyId
        AND ve.vehicleId = :vehicleId
        ORDER BY ve.year DESC, ve.month DESC
        """)
    List<VehicleEfficiencyEntity> findLatestByVehicleList(
        @Param("companyId") Long companyId,
        @Param("vehicleId") String vehicleId,
        Pageable pageable
    );

    default Optional<VehicleEfficiencyEntity> findLatestByVehicle(Long companyId, String vehicleId) {
        List<VehicleEfficiencyEntity> results = findLatestByVehicleList(companyId, vehicleId,
            org.springframework.data.domain.PageRequest.of(0, 1));
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    /**
     * 효율성 점수별 차량 분포를 조회합니다.
     *
     * @param companyId 회사 ID
     * @param year 분석 연도
     * @param month 분석 월 (0이면 모든 월)
     * @return 효율성 점수 구간별 차량 수
     */
    @Query("""
        SELECT
            CASE
                WHEN ve.inefficiencyScore >= 80 THEN 'VERY_INEFFICIENT'
                WHEN ve.inefficiencyScore >= 60 THEN 'INEFFICIENT'
                WHEN ve.inefficiencyScore >= 40 THEN 'MODERATE'
                WHEN ve.inefficiencyScore >= 20 THEN 'EFFICIENT'
                ELSE 'VERY_EFFICIENT'
            END,
            COUNT(ve),
            AVG(ve.inefficiencyScore),
            AVG(ve.costPerKm)
        FROM VehicleEfficiencyEntity ve
        WHERE ve.companyId = :companyId
        AND ve.year = :year
        AND (:month = 0 OR ve.month = :month)
        AND ve.inefficiencyScore > 0
        GROUP BY CASE
            WHEN ve.inefficiencyScore >= 80 THEN 'VERY_INEFFICIENT'
            WHEN ve.inefficiencyScore >= 60 THEN 'INEFFICIENT'
            WHEN ve.inefficiencyScore >= 40 THEN 'MODERATE'
            WHEN ve.inefficiencyScore >= 20 THEN 'EFFICIENT'
            ELSE 'VERY_EFFICIENT'
        END
        ORDER BY AVG(ve.inefficiencyScore) DESC
        """)
    List<Object[]> findEfficiencyDistribution(
        @Param("companyId") Long companyId,
        @Param("year") Integer year,
        @Param("month") Integer month
    );

    /**
     * 연비 효율성이 특정 기준 이상인 차량들을 조회합니다.
     *
     * @param companyId 회사 ID
     * @param minFuelEfficiency 최소 연비 효율성
     * @param year 분석 연도
     * @param month 분석 월 (0이면 모든 월)
     * @return 연비 효율성이 좋은 차량 리스트
     */
    @Query("""
        SELECT ve FROM VehicleEfficiencyEntity ve
        WHERE ve.companyId = :companyId
        AND ve.fuelEfficiency >= :minFuelEfficiency
        AND ve.year = :year
        AND (:month = 0 OR ve.month = :month)
        ORDER BY ve.fuelEfficiency DESC
        """)
    List<VehicleEfficiencyEntity> findHighFuelEfficiencyVehicles(
        @Param("companyId") Long companyId,
        @Param("minFuelEfficiency") BigDecimal minFuelEfficiency,
        @Param("year") Integer year,
        @Param("month") Integer month
    );

    /**
     * 차량별 효율성 개선률을 계산합니다.
     *
     * @param companyId 회사 ID
     * @param vehicleId 차량 ID (빈 문자열이면 모든 차량)
     * @param fromYear 시작 연도
     * @param fromMonth 시작 월 (0이면 모든 월)
     * @param toYear 종료 연도
     * @param toMonth 종료 월 (0이면 모든 월)
     * @return 효율성 개선률 데이터
     */
    @Query("""
        SELECT
            ve.vehicleId,
            MIN(ve.inefficiencyScore),
            MAX(ve.inefficiencyScore),
            COUNT(ve),
            (MAX(ve.inefficiencyScore) - MIN(ve.inefficiencyScore))
        FROM VehicleEfficiencyEntity ve
        WHERE ve.companyId = :companyId
        AND (:vehicleId = '' OR ve.vehicleId = :vehicleId)
        AND ((ve.year = :fromYear AND (:fromMonth = 0 OR ve.month >= :fromMonth))
             OR (ve.year > :fromYear AND ve.year < :toYear)
             OR (ve.year = :toYear AND (:toMonth = 0 OR ve.month <= :toMonth)))
        AND ve.inefficiencyScore > 0
        GROUP BY ve.vehicleId
        HAVING COUNT(ve) >= 2
        ORDER BY (MAX(ve.inefficiencyScore) - MIN(ve.inefficiencyScore)) DESC
        """)
    List<Object[]> findEfficiencyImprovement(
        @Param("companyId") Long companyId,
        @Param("vehicleId") String vehicleId,
        @Param("fromYear") Integer fromYear,
        @Param("fromMonth") Integer fromMonth,
        @Param("toYear") Integer toYear,
        @Param("toMonth") Integer toMonth
    );
}