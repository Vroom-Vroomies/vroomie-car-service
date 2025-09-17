package com.vroomie.car_service.global.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    // ========== HTTP 표준 상태 코드 ==========
    INVALID_REQUEST("유효하지 않은 요청입니다", 400, HttpStatus.BAD_REQUEST),
    UNAUTHORIZED("인증 정보가 없습니다", 401, HttpStatus.UNAUTHORIZED),
    FORBIDDEN("접근 권한이 없습니다", 403, HttpStatus.FORBIDDEN),
    INTERNAL_SERVER_ERROR("서버 오류가 발생했습니다", 500, HttpStatus.INTERNAL_SERVER_ERROR),

    // ========== 차량 정보 관련 (1000~1999) ==========
    CAR_NOT_FOUND("차량을 찾을 수 없습니다", 1000, HttpStatus.NOT_FOUND),

    // ========== 사고 관련 (1300~1399) ==========
    ACCIDENT_NOT_FOUND("사고 정보를 찾을 수 없습니다", 1300, HttpStatus.NOT_FOUND),
    CANT_FINALIZE_FALSE("최종 확정 요청은 False일 수 없습니다", 1301, HttpStatus.BAD_REQUEST),
    ACCIDENT_ALREADY_FINALIZED("이미 제출된 사고 정보는 수정할 수 없습니다", 1302, HttpStatus.BAD_REQUEST);

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
