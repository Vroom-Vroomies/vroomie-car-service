package com.vroomie.car_service.domain.fleet.drivinglog.repository;

import com.vroomie.car_service.domain.fleet.drivinglog.entity.DrivingLogEntity;
import com.vroomie.car_service.domain.fleet.drivinglog.enums.LogStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface DrivingLogRepository extends JpaRepository<DrivingLogEntity, Long>, JpaSpecificationExecutor<DrivingLogEntity> {
    boolean existsByEmployeeEntity_EmailAndCarEntity_IdAndLogStatus(String empEmail, Long carId, LogStatus logStatus);

    // 특정 사용자가 특정 차량에 대해 작성중인 가장 최신 운행기록
    Optional<DrivingLogEntity> findTopByCarEntity_IdAndEmployeeEntity_EmailAndLogStatusOrderByCreatedAtDesc(Long carId, String empEmail, LogStatus logStatus);

    // 해당 날짜 동안 특정 차량에 대해 작성이 완료된 운행기록 조회
    @Query("SELECT d FROM DrivingLogEntity d " +
            "JOIN d.carEntity c " +
            "WHERE c.id = :carId " +
            "AND d.startedAt >= :startedAt " +
            "AND d.startedAt <= :endedAt " +
            "AND d.logStatus = :logStatus " +
            "ORDER BY d.startedAt DESC")
    List<DrivingLogEntity> findAllByCarAndPeriodAndStatus(
            @Param("carId") Long carId,
            @Param("startedAt") LocalDateTime startedAt,
            @Param("endedAt") LocalDateTime endedAt,
            @Param("logStatus") LogStatus logStatus
    );
}
