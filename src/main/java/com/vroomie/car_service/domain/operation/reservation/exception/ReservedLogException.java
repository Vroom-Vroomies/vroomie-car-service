package com.vroomie.car_service.domain.operation.reservation.exception;

import com.vroomie.car_service.global.exception.BusinessException;
import com.vroomie.car_service.global.exception.ErrorCode;

public class ReservedLogException {

    public static BusinessException reservedLogNotFound(Long id) {
        return new BusinessException(ErrorCode.RESERVED_LOG_NOT_FOUND);
    }

    public static BusinessException accessDenied(String userEmail, String logOwnerEmail) {
        return new BusinessException(ErrorCode.RESERVED_LOG_ACCESS_DENIED);
    }

    public static BusinessException employeeNotFound(String email) {
        return new BusinessException(ErrorCode.EMPLOYEE_NOT_FOUND);
    }
}