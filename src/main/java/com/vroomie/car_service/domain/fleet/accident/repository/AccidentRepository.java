package com.vroomie.car_service.domain.fleet.accident.repository;

import com.vroomie.car_service.domain.fleet.accident.entity.AccidentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccidentRepository extends JpaRepository<AccidentEntity, Integer> {
}
