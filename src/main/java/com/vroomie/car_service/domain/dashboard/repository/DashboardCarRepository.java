package com.vroomie.car_service.domain.dashboard.repository;

import com.vroomie.car_service.domain.fleet.car.entity.CarEntity;
import com.vroomie.car_service.domain.dashboard.projection.VehicleOwnershipProjection;
import com.vroomie.car_service.domain.dashboard.projection.VehicleStatusProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DashboardCarRepository extends JpaRepository<CarEntity, Long> {

    /**
     * 회사별 차량 보유 현황 조회
     * @param companyId 회사 아이디
     * @return List<VehicleOwnershipProjection>
     */
    @Query("""
        SELECT c.type as type, COUNT(c) as count
        FROM CarEntity c
        WHERE c.companyId = :companyId
        GROUP BY c.type
        """)
    List<VehicleOwnershipProjection> findVehicleOwnershipByCompany(@Param("companyId") Long companyId);

    /**
     * 회사별 차량 상태 분포 조회
     * @param companyId 회사 아이디
     * @return List<VehicleStatusProjection>
     */
    @Query("""
        SELECT c.status as status, COUNT(c) as count
        FROM CarEntity c
        WHERE c.companyId = :companyId
        GROUP BY c.status
        """)
    List<VehicleStatusProjection> findVehicleStatusByCompany(@Param("companyId") Long companyId);

    /**
     * 회사가 보유한 차량 전체 수 조회
     * @param companyId 회사아이디
     * @return countByCompanyId
     */
    @Query("SELECT COUNT(c) FROM CarEntity c WHERE c.companyId = :companyId")
    Long countByCompanyId(@Param("companyId") Long companyId);
}