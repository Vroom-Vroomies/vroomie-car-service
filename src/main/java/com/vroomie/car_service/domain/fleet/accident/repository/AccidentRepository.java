package com.vroomie.car_service.domain.fleet.accident.repository;

import com.vroomie.car_service.domain.fleet.accident.entity.AccidentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AccidentRepository extends JpaRepository<AccidentEntity, Long> {

    @Query("select a from AccidentEntity a left join fetch a.accidentImages where a.id = :id")
    Optional<AccidentEntity> findByIdWithImages(@Param("id") Long id);

    @Query(value = "SELECT a FROM AccidentEntity a " +
            "LEFT JOIN FETCH a.car c " +
            "LEFT JOIN FETCH a.employee e " +
            "WHERE (:carId IS NULL OR a.car.id = :carId) " +
            "AND (:isSaved IS NULL OR a.isSaved = :isSaved)",
            countQuery = "SELECT count(a) FROM AccidentEntity a " +
                    "WHERE (:carId IS NULL OR a.car.id = :carId) " +
                    "AND (:isSaved IS NULL OR a.isSaved = :isSaved)")
    Page<AccidentEntity> findWithFilters(@Param("carId") Long carId, @Param("isSaved") Boolean isSaved, Pageable pageable);
}
