package com.vroomie.car_service.domain.fleet.car.repository;

import com.vroomie.car_service.domain.fleet.car.entity.CarEntity;
import com.vroomie.car_service.domain.fleet.car.enums.CarStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;

public interface CarRepository extends JpaRepository<CarEntity, Long> {

       @Query("SELECT c FROM CarEntity c " +
                     "WHERE c.id = :carId " +
                     "AND c.status = :status " +
                     "AND NOT EXISTS (" +
                     "    SELECT rl FROM ReservedLogEntity rl " +
                     "    WHERE rl.car = c " +
                     "    AND rl.status != com.vroomie.car_service.domain.operation.reservation.enums.RentStatus.RETURNED"
                     +
                     ")")
       CarEntity findAvailableCarById(@Param("carId") Long carId, @Param("status") CarStatus status);

       @Query("SELECT c FROM CarEntity c " +
                     "WHERE c.status = :status " +
                     "AND NOT EXISTS (" +
                     "    SELECT rl FROM ReservedLogEntity rl " +
                     "    WHERE rl.car = c " +
                     "    AND rl.status != com.vroomie.car_service.domain.operation.reservation.enums.RentStatus.RETURNED "
                     +
                     "    AND ((" +
                     "        rl.startedAt <= :requestedStartTime AND rl.endedAt > :requestedStartTime" +
                     "    ) OR (" +
                     "        rl.startedAt < :requestedEndTime AND rl.endedAt >= :requestedEndTime" +
                     "    ) OR (" +
                     "        rl.startedAt >= :requestedStartTime AND rl.endedAt <= :requestedEndTime" +
                     "    ))" +
                     ") " +
                     "ORDER BY c.id")
       Page<CarEntity> findAvailableCarsByTimeSlotWithReservedLog(@Param("status") CarStatus status,
                     @Param("requestedStartTime") LocalDateTime requestedStartTime,
                     @Param("requestedEndTime") LocalDateTime requestedEndTime,
                     Pageable pageable);
}
