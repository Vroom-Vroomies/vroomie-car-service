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
        return new BusinessException(ErrorCode.RESERVATION_NOT_FOUND);
    }

    public static BusinessException employeeNotFound(String adminEmail) {
        return new BusinessException(ErrorCode.EMPLOYEE_NOT_FOUND);
    }

    public static BusinessException carNotAvailable(Long carId) {
        return new BusinessException(ErrorCode.CAR_NOT_AVAILABLE);
    }

    public static BusinessException invalidTimeSlot() {
        return new BusinessException(ErrorCode.INVALID_TIME_SLOT);
    }

    public static BusinessException invalidDateTimeFormat() {
        return new BusinessException(ErrorCode.INVALID_DATETIME_FORMAT);
    }

    public static BusinessException reservationTimeConflict() {
        return new BusinessException(ErrorCode.RESERVATION_TIME_CONFLICT);
    }

    public static BusinessException reservationNotCancellable() {
        return new BusinessException(ErrorCode.RESERVATION_NOT_CANCELLABLE);
    }

    public static BusinessException memberReservationNotFound() {
        return new BusinessException(ErrorCode.MEMBER_RESERVATION_NOT_FOUND);
    }

    public static BusinessException memberAlreadyHasActiveRental() {
        return new BusinessException(ErrorCode.MEMBER_ALREADY_HAS_ACTIVE_RENTAL);
    }

    public static BusinessException memberAlreadyHasActiveReservation() {
        return new BusinessException(ErrorCode.MEMBER_ALREADY_HAS_ACTIVE_RESERVATION);
    }
}
