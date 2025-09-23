package com.vroomie.car_service.domain.operation.reservation.dto.member;

import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import java.time.LocalDateTime;
import com.vroomie.car_service.domain.operation.reservation.enums.Purpose;
import com.vroomie.car_service.domain.operation.reservation.enums.ReservationStatus;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CurrentCarResponse {

    private Long carId;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private Purpose purpose;
    private String detail;
    private ReservationStatus status;
}