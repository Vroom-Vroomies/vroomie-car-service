package com.vroomie.car_service.domain.operation.reservation.repository;

import com.vroomie.car_service.domain.operation.reservation.entity.ReservationEntity;
import com.vroomie.car_service.domain.operation.reservation.entity.ReservedLogEntity;
import com.vroomie.car_service.domain.operation.reservation.enums.RentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
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

        boolean existsByCarIdAndStatusIn(Long carId, List<RentStatus> statusList);

        @Query("SELECT rl FROM ReservedLogEntity rl " +
                        "JOIN FETCH rl.car c " +
                        "JOIN FETCH rl.reservation r " +
                        "LEFT JOIN FETCH rl.admin a " +
                        "WHERE rl.status = 'RENTED' " +
                        "AND rl.returnDate IS NULL " +
                        "AND rl.endedAt < :currentTime")
        List<ReservedLogEntity> findOverdueRentals(@Param("currentTime") LocalDateTime currentTime);

        @Query("SELECT rl FROM ReservedLogEntity rl " +
                        "JOIN FETCH rl.car c " +
                        "LEFT JOIN FETCH rl.admin a " +
                        "WHERE rl.reservation = :reservation " +
                        "AND rl.status = :status")
        List<ReservedLogEntity> findByReservationAndStatus(@Param("reservation") ReservationEntity reservation, 
                        @Param("status") RentStatus status);

        @Query("SELECT rl FROM ReservedLogEntity rl " +
                        "JOIN FETCH rl.car c " +
                        "JOIN FETCH rl.reservation r " +
                        "JOIN FETCH r.member m " +
                        "LEFT JOIN FETCH rl.admin a " +
                        "WHERE m.email = :memberEmail " +
                        "AND rl.status IN :statuses")
        List<ReservedLogEntity> findByMemberEmailAndStatus(@Param("memberEmail") String memberEmail,
                        @Param("statuses") List<RentStatus> statuses);
}