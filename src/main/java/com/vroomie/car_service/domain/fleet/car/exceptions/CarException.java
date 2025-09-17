package com.vroomie.car_service.domain.fleet.car.exceptions;

import com.vroomie.car_service.global.exception.BusinessException;
import com.vroomie.car_service.global.exception.ErrorCode;

public class CarException extends BusinessException {
    public CarException(ErrorCode errorCode) {
        super(errorCode);
    }

    public static CarException carNotFoundException() {
        return new CarException(ErrorCode.CAR_NOT_FOUND);
    }
}