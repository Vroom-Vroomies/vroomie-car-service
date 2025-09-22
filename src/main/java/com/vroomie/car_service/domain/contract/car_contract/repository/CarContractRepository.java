package com.vroomie.car_service.domain.contract.car_contract.repository;

import com.vroomie.car_service.domain.contract.car_contract.entity.CarContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CarContractRepository extends JpaRepository<CarContract,Long> {

    @Query("SELECT  cc.car.id " +
            "FROM   CarContract cc " +
            "WHERE  cc.car.id IN :carIds")
    Set<Long> findCarIdsWithContractIn(List<Long> carIds);

    @Query("""
        SELECT cc, cc.car.id, cc.car.model, cc.car.number
          FROM CarContract cc
         WHERE cc.car.id = :carId
    """)
    Optional<CarContract> findByCarId(@Param("carId") Long carId);

    @Query("""
        SELECT cc
          FROM CarContract cc
         WHERE cc.contractStatus IN ('NEW', 'RENEWED')
    """)
    List<CarContract> findAllActive();
}
