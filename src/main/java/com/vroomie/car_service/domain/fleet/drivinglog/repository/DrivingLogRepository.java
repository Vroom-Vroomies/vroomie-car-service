package com.vroomie.car_service.domain.fleet.drivinglog.repository;

import com.vroomie.car_service.domain.fleet.drivinglog.entity.DrivingLogEntity;
import com.vroomie.car_service.domain.fleet.drivinglog.enums.LogStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface DrivingLogRepository extends JpaRepository<DrivingLogEntity, Long>, JpaSpecificationExecutor<DrivingLogEntity> {
    boolean existsByEmployeeEntity_EmailAndCarEntity_IdAndLogStatus(String empEmail, Long carId, LogStatus logStatus);

    // 특정 사용자가 특정 차량에 대해 작성중인 가장 최신 운행기록
    Optional<DrivingLogEntity> findTopByCarEntity_IdAndEmployeeEntity_EmailAndLogStatusOrderByCreatedAtDesc(Long carId, String empEmail, LogStatus logStatus);
}
