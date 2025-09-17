package com.vroomie.car_service.domain.operation.reservation.repository;

import com.vroomie.car_service.domain.operation.reservation.entity.ReservedLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface ReservedLogRepository extends JpaRepository<ReservedLogEntity, Long> {

    @Query("SELECT  rl.car.id " +
            "FROM   ReservedLogEntity rl " +
            "WHERE  rl.car.id IN :carIds AND rl.reservedStatus = 'RENTED'")
    Set<Long> findRentedCarIdsIn(@Param("carIds") List<Long> carIds);}
}