package com.vroomie.car_service.domain.fleet.repair.repository;

import com.vroomie.car_service.domain.fleet.repair.entity.RepairImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RepairImageRepository extends JpaRepository<RepairImageEntity, Long> {

    void deleteByRepairId(Long repairId);

    List<RepairImageEntity> findByRepairId(Long repairId);
}