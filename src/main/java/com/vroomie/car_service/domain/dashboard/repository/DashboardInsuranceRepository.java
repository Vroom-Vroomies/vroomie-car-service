package com.vroomie.car_service.domain.dashboard.repository;

import com.vroomie.car_service.domain.contract.insurance_contract.entity.InsuContractEntity;
import com.vroomie.car_service.domain.dashboard.projection.AvailableFeeTypeProjection;
import com.vroomie.car_service.domain.dashboard.projection.MonthlyCostProjection;
import com.vroomie.car_service.domain.dashboard.projection.VehicleMaintenanceProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 대시보드 보험 비용 분석을 위한 리포지토리
 * 보험 계약 데이터를 기반으로 보험료 분석 데이터를 제공합니다.
 */
@Repository
public interface DashboardInsuranceRepository extends JpaRepository<InsuContractEntity, Long> {

    /**
     * 보험 비용(고정 비용)을 조회합니다.
     * 현재 활성 상태인 보험 계약들의 보험료를 집계합니다.
     *
     * @param companyId 회사 ID
     * @return 월별 보험 비용 리스트
     */
    @Query("""
        SELECT
            DATE_FORMAT(CURRENT_DATE, '%Y-%m') as month,
            'insurance' as feeType,
            SUM(ic.premium) as amount,
            'insurance' as source
        FROM InsuContractEntity ic
        JOIN ic.car c
        WHERE c.companyId = :companyId
        AND ic.insuranceStatus = 'ACTIVE'
        AND ic.startDate <= CURRENT_DATE
        AND ic.endDate >= CURRENT_DATE
        """)
    List<MonthlyCostProjection> findInsuranceCosts(@Param("companyId") Long companyId);

    /**
     * 차량별 보험 비용을 조회합니다.
     * 각 차량의 활성 보험 계약에 따른 보험료를 반환합니다.
     *
     * @param companyId 회사 ID
     * @param vehicleIds 조회할 차량 ID 리스트 (null이면 전체 차량)
     * @return 차량별 보험 비용 리스트
     */
    @Query("""
        SELECT
            c.number as vehicleId,
            c.model as vehicleName,
            'insurance' as feeType,
            ic.premium as amount,
            DATE_FORMAT(CURRENT_DATE, '%Y-%m') as period,
            'insurance' as source
        FROM InsuContractEntity ic
        JOIN ic.car c
        WHERE c.companyId = :companyId
        AND ic.insuranceStatus = 'ACTIVE'
        AND ic.startDate <= CURRENT_DATE
        AND ic.endDate >= CURRENT_DATE
        AND (:vehicleIds IS NULL OR c.number IN :vehicleIds)
        """)
    List<VehicleMaintenanceProjection> findVehicleInsuranceCosts(
        @Param("companyId") Long companyId,
        @Param("vehicleIds") List<String> vehicleIds
    );

    /**
     * 보험 유형을 발견합니다.
     * 활성 보험 계약이 있는 경우 insurance 비용 유형이 사용 중임을 나타냅니다.
     *
     * @param companyId 회사 ID
     * @return 사용 중인 보험 비용 유형 리스트
     */
    @Query("""
        SELECT DISTINCT
            'insurance' as feeType,
            'insurance' as source,
            SUM(ic.premium) as totalAmount
        FROM InsuContractEntity ic
        JOIN ic.car c
        WHERE c.companyId = :companyId
        AND ic.insuranceStatus = 'ACTIVE'
        AND ic.startDate <= CURRENT_DATE
        AND ic.endDate >= CURRENT_DATE
        """)
    List<AvailableFeeTypeProjection> findAvailableInsuranceFeeTypes(@Param("companyId") Long companyId);

    /**
     * 월별 보험료 납부 내역을 조회합니다.
     * 보험료 납부 유형별로 월별 비용을 분석합니다.
     *
     * @param companyId 회사 ID
     * @return 월별 보험료 내역 리스트
     */
    @Query("""
        SELECT
            DATE_FORMAT(CURRENT_DATE, '%Y-%m') as month,
            CONCAT('insurance_', ic.paymentType) as feeType,
            SUM(CASE
                WHEN ic.paymentType = 'MONTHLY' THEN ic.premium
                WHEN ic.paymentType = 'QUARTERLY' THEN ic.premium / 3
                WHEN ic.paymentType = 'SEMI_ANNUAL' THEN ic.premium / 6
                WHEN ic.paymentType = 'ANNUAL' THEN ic.premium / 12
                ELSE ic.premium
            END) as amount,
            'insurance' as source
        FROM InsuContractEntity ic
        JOIN ic.car c
        WHERE c.companyId = :companyId
        AND ic.insuranceStatus = 'ACTIVE'
        AND ic.startDate <= CURRENT_DATE
        AND ic.endDate >= CURRENT_DATE
        GROUP BY ic.paymentType
        """)
    List<MonthlyCostProjection> findMonthlyInsuranceCosts(@Param("companyId") Long companyId);
}