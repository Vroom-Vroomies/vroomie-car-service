package com.vroomie.car_service.domain.fleet.accident.repository;

import com.vroomie.car_service.domain.fleet.accident.entity.AccidentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccidentRepository extends JpaRepository<AccidentEntity, Integer> {

    Optional<AccidentEntity> findById(Long id);
}
