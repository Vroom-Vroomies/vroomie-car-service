package com.vroomie.car_service.domain.contract.insurance_contract.repository;

import com.vroomie.car_service.domain.contract.insurance_contract.entity.InsuContractEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface InsuContractRepository extends JpaRepository<InsuContractEntity, Long> {

    @Query("SELECT  ic.car.id " +
            "FROM   InsuContractEntity ic " +
            "WHERE  ic.car.id IN :carIds")
    Set<Long> findCarIdsWithInsuranceIn(List<Long> carIds);
}
