package com.vroomie.car_service.domain.dashboard.repository;

import com.vroomie.car_service.domain.contract.car_contract.entity.CarContract;
import com.vroomie.car_service.domain.dashboard.projection.AvailableFeeTypeProjection;
import com.vroomie.car_service.domain.dashboard.projection.MonthlyCostProjection;
import com.vroomie.car_service.domain.dashboard.projection.VehicleMaintenanceProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 대시보드 계약 비용 분석을 위한 리포지토리
 * 차량 계약 데이터를 기반으로 고정 비용 분석 데이터를 제공합니다.
 */
@Repository
public interface DashboardContractRepository extends JpaRepository<CarContract, Long> {

    /**
     * 계약 비용(고정 비용)을 조회합니다.
     * 현재 활성 상태인 계약들의 월 고정비용을 집계합니다.
     *
     * @param companyId 회사 ID
     * @return 월별 계약 비용 리스트
     */
    @Query("""
        SELECT
            DATE_FORMAT(CURRENT_DATE, '%Y-%m') as month,
            'contract' as feeType,
            SUM(cc.monthlyFee) as amount,
            'contract' as source
        FROM CarContract cc
        JOIN cc.car c
        WHERE c.companyId = :companyId
        AND cc.contractStatus = 'ACTIVE'
        AND cc.startAt <= CURRENT_DATE
        AND cc.endAt >= CURRENT_DATE
        """)
    List<MonthlyCostProjection> findContractCosts(@Param("companyId") Long companyId);

    /**
     * 차량별 계약 비용을 조회합니다.
     * 각 차량의 활성 계약에 따른 월 고정비용을 반환합니다.
     *
     * @param companyId 회사 ID
     * @param vehicleIds 조회할 차량 ID 리스트 (null이면 전체 차량)
     * @return 차량별 계약 비용 리스트
     */
    @Query("""
        SELECT
            c.number as vehicleId,
            c.model as vehicleName,
            'contract' as feeType,
            cc.monthlyFee as amount,
            DATE_FORMAT(CURRENT_DATE, '%Y-%m') as period,
            'contract' as source
        FROM CarContract cc
        JOIN cc.car c
        WHERE c.companyId = :companyId
        AND cc.contractStatus = 'ACTIVE'
        AND cc.startAt <= CURRENT_DATE
        AND cc.endAt >= CURRENT_DATE
        AND (:vehicleIds IS NULL OR c.number IN :vehicleIds)
        """)
    List<VehicleMaintenanceProjection> findVehicleContractCosts(
        @Param("companyId") Long companyId,
        @Param("vehicleIds") List<String> vehicleIds
    );

    /**
     * 계약 유형을 발견합니다.
     * 활성 계약이 있는 경우 contract 비용 유형이 사용 중임을 나타냅니다.
     *
     * @param companyId 회사 ID
     * @return 사용 중인 계약 비용 유형 리스트
     */
    @Query("""
        SELECT DISTINCT
            'contract' as feeType,
            'contract' as source,
            SUM(cc.monthlyFee) as totalAmount
        FROM CarContract cc
        JOIN cc.car c
        WHERE c.companyId = :companyId
        AND cc.contractStatus = 'ACTIVE'
        AND cc.startAt <= CURRENT_DATE
        AND cc.endAt >= CURRENT_DATE
        """)
    List<AvailableFeeTypeProjection> findAvailableContractFeeTypes(@Param("companyId") Long companyId);

    /**
     * 특정 기간 동안의 고정 계약 비용을 조회합니다.
     * 월별 분석을 위해 활성 계약들의 고정비용을 반환합니다.
     *
     * @param companyId 회사 ID
     * @return 고정 계약 비용 리스트
     */
    @Query("""
        SELECT
            DATE_FORMAT(CURRENT_DATE, '%Y-%m') as month,
            'contract' as feeType,
            SUM(cc.monthlyFee) as amount,
            'contract' as source
        FROM CarContract cc
        JOIN cc.car c
        WHERE c.companyId = :companyId
        AND cc.contractStatus = 'ACTIVE'
        AND cc.startAt <= CURRENT_DATE
        AND cc.endAt >= CURRENT_DATE
        GROUP BY DATE_FORMAT(CURRENT_DATE, '%Y-%m')
        """)
    List<MonthlyCostProjection> findFixedContractCosts(@Param("companyId") Long companyId);
}