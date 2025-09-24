package com.vroomie.car_service.domain.fleet.car.repository;

import com.vroomie.car_service.domain.fleet.car.entity.CarEntity;
import com.vroomie.car_service.domain.fleet.car.enums.CarStatus;
import com.vroomie.car_service.domain.fleet.car.enums.CarUsageType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;

public interface CarRepository extends JpaRepository<CarEntity, Long> {

  Page<CarEntity> findAllByStatusAndUsageType(CarStatus status, CarUsageType usageType, Pageable pageable);

  Page<CarEntity> findAllByStatus(CarStatus status, Pageable pageable);

  Page<CarEntity> findAllByUsageType(CarUsageType usageType, Pageable pageable);

  boolean existsByNumber(String number);

  @Query("SELECT c FROM CarEntity c " +
      "WHERE c.id = :carId " +
      "AND c.status = :status ")
  CarEntity findAvailableCarById(@Param("carId") Long carId, @Param("status") CarStatus status);

  @Query("""
          SELECT c
            FROM CarEntity c
            LEFT JOIN CarContract cc ON c.id = cc.car.id
           WHERE cc.car.id IS NULL
             AND c.status IN ('ACTIVE')
      """)
  List<CarEntity> findAllCarsContractable();

  @Query("""
          SELECT c
            FROM CarEntity c
            LEFT JOIN InsuContractEntity ic ON ic.car.id = c.id
           WHERE c.status = 'ACTIVE'
             AND ic.car.id IS NULL
      """)
  List<CarEntity> getCarListInsurable();

  @Query("SELECT c FROM CarEntity c " +
      "WHERE c.status = :status " +
      "AND NOT EXISTS (" +
      "    SELECT rl FROM ReservedLogEntity rl " +
      "    WHERE rl.car = c " +
      "    AND rl.status IN (com.vroomie.car_service.domain.operation.reservation.enums.RentStatus.RENTED, " +
      "                     com.vroomie.car_service.domain.operation.reservation.enums.RentStatus.OVERDUE, " +
      "                     com.vroomie.car_service.domain.operation.reservation.enums.RentStatus.RESERVED) " +
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

  // 비관적 락을 사용하여 차량 조회 (예약 생성 시 동시성 제어용)
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT c FROM CarEntity c WHERE c.id = :carId AND c.status = :status")
  CarEntity findByIdAndStatusWithLock(@Param("carId") Long carId, @Param("status") CarStatus status);

}
