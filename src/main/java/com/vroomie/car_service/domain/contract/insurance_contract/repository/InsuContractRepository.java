package com.vroomie.car_service.domain.contract.insurance_contract.repository;

import com.vroomie.car_service.domain.contract.insurance_contract.entity.InsuContractEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface InsuContractRepository extends JpaRepository<InsuContractEntity, Long> {

    @Query("SELECT  ic.car.id " +
            "FROM   InsuContractEntity ic " +
            "WHERE  ic.car.id IN :carIds")
    Set<Long> findCarIdsWithInsuranceIn(List<Long> carIds);

    @Query("""
        SELECT ic, ic.car.id, ic.car.number
          FROM InsuContractEntity ic
         WHERE ic.car.id = :carId
    """)
    List<InsuContractEntity> findAllByCarId(@Param("carId") Long carId);

    @Query("""
        SELECT ic
          FROM InsuContractEntity ic
         WHERE ic.insuranceStatus IN ('NEW', 'RENEWED')
    """)
    List<InsuContractEntity> findAllActive();

    @Query("""
        SELECT ic
          FROM InsuContractEntity ic
         WHERE ic.insuranceStatus IN ('NEW', 'RENEWED')
           AND ic.paymentDay = :todayDayOfMonth
    """)
    List<InsuContractEntity> findAllActiveInsurancesByPaymentDay(@Param("todayDayOfMonth") int todayDayOfMonth);
}
