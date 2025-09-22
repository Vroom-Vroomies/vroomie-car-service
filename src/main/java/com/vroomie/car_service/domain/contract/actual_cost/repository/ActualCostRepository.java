package com.vroomie.car_service.domain.contract.actual_cost.repository;

import com.vroomie.car_service.domain.contract.actual_cost.entity.ActualCost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActualCostRepository extends JpaRepository<ActualCost, Long> {
}
