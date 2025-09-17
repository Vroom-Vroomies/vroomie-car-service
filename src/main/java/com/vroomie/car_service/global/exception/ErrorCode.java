package com.vroomie.car_service.global.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    // ========== HTTP 표준 상태 코드 ==========
    INVALID_REQUEST("유효하지 않은 요청입니다", 400, HttpStatus.BAD_REQUEST),
    UNAUTHORIZED("인증 정보가 없습니다", 401, HttpStatus.UNAUTHORIZED),
    FORBIDDEN("접근 권한이 없습니다", 403, HttpStatus.FORBIDDEN),
    INTERNAL_SERVER_ERROR("서버 오류가 발생했습니다", 500, HttpStatus.INTERNAL_SERVER_ERROR),

    // ========== 차량 정보 관련 (1000~1099) ==========
    CAR_NOT_FOUND("차량을 찾을 수 없습니다", 1000, HttpStatus.NOT_FOUND),

    // ========== 유저 관련 (1100~1199) ==========
    EMPLOYEE_NOT_FOUND("존재하지 않는 직원입니다.", 1100, HttpStatus.NOT_FOUND),

    // ========== 운행기록 관련 (1200~1299) ==========
    DRIVING_LOG_ALREADY_EXSITS("운행 기록이 이미 존재합니다. 기존 운행기록을 이어서 작성해주세요.", 1200, HttpStatus.BAD_REQUEST);


    private final String message;
    private final int statusCode;
    private final HttpStatus status;

    ErrorCode(String message, int statusCode, HttpStatus status) {
        this.message = message;
        this.statusCode = statusCode;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
