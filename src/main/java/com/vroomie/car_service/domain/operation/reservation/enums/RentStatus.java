package com.vroomie.car_service.domain.operation.reservation.enums;

public enum RentStatus {
    RENTED,
    RETURNED,
    OVERDUE, // 연체
    RESERVED, // 예약 중
    CANCELLED // 취소
}
