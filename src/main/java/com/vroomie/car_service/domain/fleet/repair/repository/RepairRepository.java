package com.vroomie.car_service.domain.fleet.repair.repository;

import com.vroomie.car_service.domain.fleet.log.projection.CarLogProjection;
import com.vroomie.car_service.domain.fleet.repair.entity.RepairEntity;
import com.vroomie.car_service.domain.fleet.repair.enums.RepairStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RepairRepository extends JpaRepository<RepairEntity, Long> {

    @Query("""
           SELECT r
           FROM RepairEntity r
           LEFT JOIN fetch r.repairImages WHERE r.id = :id
           """)
    Optional<RepairEntity> findByIdWithImages(@Param("id") Long id);

    @Query(value = """
            SELECT r FROM RepairEntity r
            LEFT JOIN FETCH r.car c
            LEFT JOIN FETCH r.employee e
            WHERE (:carId IS NULL OR r.car.id = :carId)
            AND (:status IS NULL OR r.status = :status)
            AND (:isSaved IS NULL OR r.isSaved = :isSaved)
            """,
            countQuery = """
            SELECT count(r) FROM RepairEntity r
            WHERE (:carId IS NULL OR r.car.id = :carId)
            AND (:status IS NULL OR r.status = :status)
            AND (:isSaved IS NULL OR r.isSaved = :isSaved)
            """)
    Page<RepairEntity> findWithFilters(
            @Param("carId") Long carId,
            @Param("status") RepairStatus status,
            @Param("isSaved") Boolean isSaved,
            Pageable pageable
    );

    // 수리중인 차량 목록
    @Query("SELECT  r.car.id " +
            "FROM    RepairEntity r " +
            "WHERE  r.car.id IN :carIds" +
            "   AND r.status = 'IN_REPAIR'")
    Set<Long> findCarIdsWithActiveRepairsIn(@Param("carIds") List<Long> carIds);

    @Query("""
        SELECT r.id as logId,
               'REPAIR' as logType,
               r.startedAt as date,
               r.detail as description,
               e.name as handler,
               r.status as status,
               r.cost as cost
        FROM RepairEntity r
        LEFT JOIN EmployeeEntity e ON r.employee.email = e.email
        WHERE r.car.id = :carId
        """)
    Page<CarLogProjection> findRepairLogsByCarId(@Param("carId") Long carId, Pageable pageable);
}