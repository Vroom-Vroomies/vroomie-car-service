package com.vroomie.car_service.domain.contract.actual_cost.repository;

import com.vroomie.car_service.domain.contract.actual_cost.entity.ActualCost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Date;


public interface ActualCostRepository extends JpaRepository<ActualCost, Long> {

    boolean existsByContractIdAndCostDateBetween(Long id, Date date, Date date1);
}
