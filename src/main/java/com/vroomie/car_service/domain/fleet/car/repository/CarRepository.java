package com.vroomie.car_service.domain.fleet.car.repository;

import com.vroomie.car_service.domain.fleet.car.entity.CarEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<CarEntity, Long> {
}
