package com.vroomie.car_service.domain.fleet.accident.repository;

import com.vroomie.car_service.domain.fleet.accident.entity.AccidentImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccidentImageRepository extends JpaRepository<AccidentImageEntity, Long> {
    void deleteByAccidentId(Long accidentId);

    List<AccidentImageEntity> findByAccidentId(Long accidentId);
}
