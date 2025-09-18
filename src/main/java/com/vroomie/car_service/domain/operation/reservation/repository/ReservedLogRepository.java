package com.vroomie.car_service.domain.operation.reservation.repository;

import com.vroomie.car_service.domain.operation.reservation.entity.ReservedLogEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservedLogRepository extends JpaRepository<ReservedLogEntity, Long> {
    
    @Query("SELECT rl FROM ReservedLogEntity rl " +
           "JOIN FETCH rl.car c " +
           "JOIN FETCH rl.reservation r " +
           "JOIN FETCH r.member m " +
           "WHERE m.email = :memberEmail " +
           "ORDER BY rl.createdAt DESC")
    Page<ReservedLogEntity> findByMemberEmailOrderByCreatedAtDesc(@Param("memberEmail") String memberEmail, Pageable pageable);
    
    @Query("SELECT rl FROM ReservedLogEntity rl " +
           "JOIN FETCH rl.car c " +
           "JOIN FETCH rl.reservation r " +
           "JOIN FETCH r.member m " +
           "WHERE rl.id = :id AND m.email = :memberEmail")
    ReservedLogEntity findByIdAndMemberEmail(@Param("id") Long id, @Param("memberEmail") String memberEmail);
    
}