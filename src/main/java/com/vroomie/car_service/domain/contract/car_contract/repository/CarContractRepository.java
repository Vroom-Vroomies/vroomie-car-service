package com.vroomie.car_service.domain.contract.car_contract.repository;

import com.vroomie.car_service.domain.contract.car_contract.entity.CarContract;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarContractRepository extends JpaRepository<CarContract,Long> {

}
