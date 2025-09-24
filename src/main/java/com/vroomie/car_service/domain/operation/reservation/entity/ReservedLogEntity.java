package com.vroomie.car_service.domain.operation.reservation.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import org.hibernate.annotations.BatchSize;
import com.vroomie.car_service.global.constants.BusinessConstants;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.AccessLevel;
import lombok.Builder;
import java.time.LocalDateTime;
import com.vroomie.car_service.domain.employee.entity.EmployeeEntity;
import com.vroomie.car_service.domain.fleet.car.entity.CarEntity;
import com.vroomie.car_service.domain.operation.reservation.enums.RentStatus;

@Entity
@Table(name = "tbl_reserved_log")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ReservedLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id")
    @BatchSize(size = BusinessConstants.DEFAULT_BATCH_SIZE)
    private CarEntity car;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_emp_email")
    @BatchSize(size = BusinessConstants.DEFAULT_BATCH_SIZE)
    private EmployeeEntity admin;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    @BatchSize(size = BusinessConstants.DEFAULT_BATCH_SIZE)
    private ReservationEntity reservation;

    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

    @Enumerated(EnumType.STRING)
    private RentStatus status;

    private LocalDateTime returnDate;
    private LocalDateTime createdAt;

    public void updateStatus(RentStatus newStatus) {
        this.status = newStatus;
    }

    public void updateAdmin(EmployeeEntity newAdmin) {
        this.admin = newAdmin;
    }
}
