package com.vroomie.car_service.domain.contract.car_contract.repository;

import com.vroomie.car_service.domain.contract.car_contract.entity.CarContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Set;

public interface CarContractRepository extends JpaRepository<CarContract,Long> {

    @Query("SELECT  cc.car.id " +
            "FROM   CarContract cc " +
            "WHERE  cc.car.id IN :carIds")
    Set<Long> findCarIdsWithContractIn(List<Long> carIds);
}
