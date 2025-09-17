package com.vroomie.car_service.domain.operation.reservation.dto.admin;

import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import com.vroomie.car_service.domain.operation.reservation.enums.ReservationStatus;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdminReservationRequest {
    private ReservationStatus reservationStatus; // 예약 상태(승인 여부)
}