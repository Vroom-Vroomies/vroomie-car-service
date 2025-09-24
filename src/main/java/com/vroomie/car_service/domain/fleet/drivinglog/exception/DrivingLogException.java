package com.vroomie.car_service.domain.fleet.drivinglog.exception;

import com.vroomie.car_service.global.exception.BusinessException;
import com.vroomie.car_service.global.exception.ErrorCode;

public class DrivingLogException extends BusinessException {
    public DrivingLogException(ErrorCode errorCode) {
        super(errorCode);
    }

    public static DrivingLogException alreadyExistsLogException() {
        return new DrivingLogException(ErrorCode.DRIVING_LOG_ALREADY_EXSITS);
    }

    public static DrivingLogException drivingLogNotFoundException() {
        return new DrivingLogException(ErrorCode.DRIVING_LOG_NOT_FOUND);
    }

    public static DrivingLogException drivingLogListNotFoundException() {
        return new DrivingLogException(ErrorCode.DRIVING_LOG_LIST_NOT_FOUND);
    }

    public static DrivingLogException invalidEndOdometerException() {
        return new DrivingLogException(ErrorCode.INVALID_END_ODOMETER);
    }

    public static DrivingLogException alreadySubmitsLogException() {
        return new DrivingLogException(ErrorCode.DRIVING_LOG_ALREADY_SUBMITS);
    }

    public static DrivingLogException invalidEndDateException() {
        return new DrivingLogException(ErrorCode.INVALID_END_DATE);
    }
}
