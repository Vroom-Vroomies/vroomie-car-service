package com.vroomie.car_service.domain.fleet.car.repository;

import com.vroomie.car_service.domain.fleet.car.entity.CarEntity;
import com.vroomie.car_service.domain.fleet.car.enums.CarStatus;
import com.vroomie.car_service.domain.fleet.car.enums.CarUsageType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<CarEntity, Long> {

    Page<CarEntity> findAllByStatusAndUsageType(CarStatus status, CarUsageType usageType, Pageable pageable);

    Page<CarEntity> findAllByStatus(CarStatus status, Pageable pageable);

    Page<CarEntity> findAllByUsageType(CarUsageType usageType, Pageable pageable);

    boolean existsByNumber(String number);
}
