package com.vroomie.car_service.domain.fleet.inspection.respository;

import com.vroomie.car_service.domain.fleet.inspection.entity.InspectionEntity;
import com.vroomie.car_service.domain.fleet.log.projection.CarLogProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InspectionRepository extends JpaRepository<InspectionEntity, Long> {

    Optional<InspectionEntity> findById(Long id);

    @Query("""
        SELECT i.id as logId,
               'INSPECTION' as logType,
               i.date as date,
               i.inspectionType as description,
               i.inspectorName as handler,
               i.finalResult as status,
               CAST(null as java.math.BigDecimal) as cost
        FROM InspectionEntity i
        WHERE i.car.id = :carId
        """)
    Page<CarLogProjection> findInspectionLogsByCarId(@Param("carId") Long carId, Pageable pageable);
}
