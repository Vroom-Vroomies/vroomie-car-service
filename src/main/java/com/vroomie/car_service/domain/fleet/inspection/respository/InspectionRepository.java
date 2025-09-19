package com.vroomie.car_service.domain.fleet.inspection.respository;

import com.vroomie.car_service.domain.fleet.inspection.entity.InspectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InspectionRepository extends JpaRepository<InspectionEntity, Long> {

    Optional<InspectionEntity> findById(Long id);
}
