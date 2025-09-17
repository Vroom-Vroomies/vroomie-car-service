package com.vroomie.car_service.domain.operation.reservation.entity;

import com.vroomie.car_service.domain.fleet.car.entity.CarEntity;
import com.vroomie.car_service.domain.employee.entity.EmployeeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import com.vroomie.car_service.domain.operation.reservation.enums.ReservationStatus;

@Entity
@Table(name = "tbl_reservation")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReservationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id")
    private CarEntity car;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_emp_email")
    private EmployeeEntity member;

    @OneToOne(mappedBy = "reservation", fetch = FetchType.LAZY)
    private ReservedLogEntity reservedLog;

    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    private String purpose;
    private LocalDateTime createdAt;

    public void updateStatus(ReservationStatus newStatus) {
        this.status = newStatus;
    }
}
