package com.vroomie.car_service.domain.employee.excpetion;

import com.vroomie.car_service.global.exception.BusinessException;
import com.vroomie.car_service.global.exception.ErrorCode;

public class EmployeeException extends BusinessException {
    public EmployeeException(ErrorCode errorCode) {
        super(errorCode);
    }

    public static EmployeeException employeeNotFoundException() {
        return new EmployeeException(ErrorCode.EMPLOYEE_NOT_FOUND);
    }
}
