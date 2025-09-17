package com.vroomie.car_service.domain.operation.reservation.repository;

import com.vroomie.car_service.domain.operation.reservation.entity.ReservedLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservedLogRepository extends JpaRepository<ReservedLogEntity, Long> {
    
}