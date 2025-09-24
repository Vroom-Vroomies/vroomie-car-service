package com.vroomie.car_service.domain.operation.reservation.repository;

import com.vroomie.car_service.domain.operation.reservation.entity.ReservationEntity;
import com.vroomie.car_service.domain.operation.reservation.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {

    Page<ReservationEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<ReservationEntity> findByCarIdOrderByCreatedAtDesc(Long carId, Pageable pageable);

    @Query("SELECT r FROM ReservationEntity r WHERE r.car.id = :carId AND r.member.email = :memberEmail ORDER BY r.createdAt DESC LIMIT 1")
    ReservationEntity findLatestByCarAndMember(@Param("carId") Long carId, @Param("memberEmail") String memberEmail);

    @Query("SELECT COUNT(r) FROM ReservationEntity r WHERE r.car.id = :carId " +
            "AND r.status IN (:statuses) " +
            "AND ((r.startedAt <= :endedAt AND r.endedAt >= :startedAt))")
    long countConflictingReservations(@Param("carId") Long carId,
            @Param("startedAt") LocalDateTime startedAt,
            @Param("endedAt") LocalDateTime endedAt,
            @Param("statuses") List<ReservationStatus> statuses);

    @Query("SELECT r FROM ReservationEntity r WHERE r.car.id = :carId AND r.member.email = :memberEmail " +
            "AND r.status = :status ORDER BY r.createdAt DESC LIMIT 1")
    ReservationEntity findLatestByCarAndMemberAndStatus(@Param("carId") Long carId,
            @Param("memberEmail") String memberEmail,
            @Param("status") ReservationStatus status);

    @Query("SELECT COUNT(r) FROM ReservationEntity r " +
            "JOIN r.member m " +
            "WHERE m.email = :memberEmail " +
            "AND r.status = 'PENDING'")
    long countActiveReservationsByMemberEmail(@Param("memberEmail") String memberEmail);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM ReservationEntity r WHERE r.id = :id")
    ReservationEntity findByIdWithLock(@Param("id") Long id);

}