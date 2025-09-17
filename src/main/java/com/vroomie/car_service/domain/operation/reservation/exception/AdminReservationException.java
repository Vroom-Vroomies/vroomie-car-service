package com.vroomie.car_service.domain.operation.reservation.exception;

import com.vroomie.car_service.domain.operation.reservation.enums.ReservationStatus;
import com.vroomie.car_service.global.exception.BusinessException;
import com.vroomie.car_service.global.exception.ErrorCode;

public class AdminReservationException {

    public static void validateStatusChange(ReservationStatus currentStatus, ReservationStatus newStatus) {
        // PENDING에서만 APPROVED 또는 REJECTED로 변경 가능
        if (currentStatus != ReservationStatus.PENDING) {
            throw new BusinessException(ErrorCode.INVALID_STATUS_CHANGE);
        }

        // PENDING 상태에서는 APPROVED 또는 REJECTED로만 변경 가능
        if (newStatus != ReservationStatus.APPROVED && newStatus != ReservationStatus.REJECTED) {
            throw new BusinessException(ErrorCode.INVALID_STATUS_CHANGE_TYPE);
        }
    }

    public static BusinessException reservationNotFound(Long id) {
        // 예약이 존재하지 않을 때 예외 발생
        return new BusinessException(ErrorCode.RESERVATION_NOT_FOUND);
    }

    public static BusinessException employeeNotFound(String adminEmail) {
        // 직원(관리자) 정보가 존재하지 않을 때 예외 발생
        return new BusinessException(ErrorCode.EMPLOYEE_NOT_FOUND);
    }
}
