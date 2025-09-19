package com.vroomie.car_service.domain.operation.reservation.repository;

import com.vroomie.car_service.domain.operation.reservation.entity.ReservedLogEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface ReservedLogRepository extends JpaRepository<ReservedLogEntity, Long> {

    @Query("SELECT  rl.car.id " +
            "FROM   ReservedLogEntity rl " +
            "WHERE  rl.car.id IN :carIds AND rl.status = 'RENTED'")
    Set<Long> findRentedCarIdsIn(@Param("carIds") List<Long> carIds);

       @Query("SELECT rl FROM ReservedLogEntity rl " +
                     "JOIN FETCH rl.car c " +
                     "JOIN FETCH rl.reservation r " +
                     "JOIN FETCH r.member m " +
                     "WHERE m.email = :memberEmail " +
                     "ORDER BY rl.createdAt DESC")
       Page<ReservedLogEntity> findByMemberEmailOrderByCreatedAtDesc(@Param("memberEmail") String memberEmail,
                     Pageable pageable);

       @Query("SELECT rl FROM ReservedLogEntity rl " +
                     "JOIN FETCH rl.car c " +
                     "JOIN FETCH rl.reservation r " +
                     "JOIN FETCH r.member m " +
                     "WHERE rl.id = :id AND m.email = :memberEmail")
       ReservedLogEntity findByIdAndMemberEmail(@Param("id") Long id, @Param("memberEmail") String memberEmail);

       @Query("SELECT COUNT(rl) FROM ReservedLogEntity rl " +
                     "JOIN rl.reservation r " +
                     "JOIN r.member m " +
                     "WHERE m.email = :memberEmail " +
                     "AND rl.status IN ('RENTED', 'OVERDUE')")
       long countActiveRentalsByMemberEmail(@Param("memberEmail") String memberEmail);
}