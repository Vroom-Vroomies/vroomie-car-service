package com.vroomie.car_service.domain.fleet.drivinglog.repository;

import com.vroomie.car_service.domain.fleet.drivinglog.entity.DrivingLogEntity;
import com.vroomie.car_service.domain.fleet.drivinglog.enums.LogStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DrivingLogRepository extends JpaRepository<DrivingLogEntity, Long>, JpaSpecificationExecutor<DrivingLogEntity> {
    boolean existsByEmployeeEntity_EmailAndCarEntity_IdAndLogStatus(String empEmail, Long carId, LogStatus logStatus);
}
