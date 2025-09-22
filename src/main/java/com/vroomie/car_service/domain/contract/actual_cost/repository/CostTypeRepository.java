package com.vroomie.car_service.domain.contract.actual_cost.repository;

import com.vroomie.car_service.domain.contract.actual_cost.entity.ContractCostType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CostTypeRepository extends JpaRepository<ContractCostType, Long> {

    @Query("""
        SELECT cct
          FROM ContractCostType cct
         WHERE cct.name LIKE CONCAT('%', :contractType, '%')
    """)
    public ContractCostType findByContractTypeName(@Param("contractType") String contractTypeName);
}
