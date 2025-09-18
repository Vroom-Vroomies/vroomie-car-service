package com.vroomie.car_service.domain.operation.reservation.repository;

import com.vroomie.car_service.domain.operation.reservation.entity.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {

    Page<ReservationEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT r FROM ReservationEntity r WHERE r.car.id = :carId AND r.member.email = :memberEmail ORDER BY r.createdAt DESC LIMIT 1")
    ReservationEntity findLatestByCarAndMember(@Param("carId") Long carId, @Param("memberEmail") String memberEmail);

}