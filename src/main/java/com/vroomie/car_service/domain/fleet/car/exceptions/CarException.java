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

    public static CarException invalidSearchParameterException() { return new CarException(ErrorCode.INVALID_CAR_SEARCH_PARAMETER); }

    public static CarException carAlreadyExistsException() { return new CarException(ErrorCode.CAR_ALREADY_EXISTS); }
}