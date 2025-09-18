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
    INVALID_CAR_SEARCH_PARAMETER("유효하지 않은 차량 검색 파라미터입니다.", 1001, HttpStatus.BAD_REQUEST),
    CAR_ALREADY_EXISTS("이미 등록된 차량입니다.", 1002, HttpStatus.CONFLICT),

    // ========== 유저 관련 (1100~1199) ==========
    EMPLOYEE_NOT_FOUND("존재하지 않는 직원입니다.", 1100, HttpStatus.NOT_FOUND),
    EMPLOYEE_NOT_MATCH("해당 운행일지의 작성자가 아닙니다.", 1101, HttpStatus.BAD_REQUEST),
    EMPLOYEE_NOT_ENOUGH_ROLE("운행일지 조회 권한이 없습니다", 1102, HttpStatus.BAD_REQUEST),

    // ========== 운행기록 관련 (1200~1299) ==========
    DRIVING_LOG_ALREADY_EXSITS("운행 기록이 이미 존재합니다. 기존 운행기록을 이어서 작성해주세요.", 1200, HttpStatus.BAD_REQUEST),
    DRIVING_LOG_NOT_FOUND("존재하지 않는 운행기록입니다.", 1201, HttpStatus.NOT_FOUND),
    INVALID_END_ODOMETER("종료 주행 거리는 시작 주행거리보다 커야합니다.", 1202, HttpStatus.BAD_REQUEST),
    DRIVING_LOG_ALREADY_SUBMITS("이미 제출된 운행일지입니다..", 1203, HttpStatus.BAD_REQUEST),

    // ========== 예약 정보 관련 (1300~1399) ==========
    RESERVATION_NOT_FOUND("예약 정보를 찾을 수 없습니다.", 1300, HttpStatus.NOT_FOUND),
    INVALID_STATUS_CHANGE("예약 상태가 PENDING일 때만 상태 변경이 가능합니다.", 1301, HttpStatus.BAD_REQUEST),
    INVALID_STATUS_CHANGE_TYPE("PENDING 상태에서는 APPROVED 또는 REJECTED로만 변경할 수 있습니다.", 1302, HttpStatus.BAD_REQUEST),

    // ========== 사고 관련 (1400~1499) ==========
    ACCIDENT_NOT_FOUND("사고 정보를 찾을 수 없습니다", 1400, HttpStatus.NOT_FOUND),
    CANT_FINALIZE_FALSE("최종 확정 요청은 False일 수 없습니다", 1401, HttpStatus.BAD_REQUEST),
    ACCIDENT_ALREADY_FINALIZED("이미 제출된 사고 정보는 수정할 수 없습니다", 1402, HttpStatus.BAD_REQUEST),
    IMAGE_REQUIRED_FOR_FINALIZE("최종 제출을 위해서는 이미지가 최소 1장 이상 필요합니다.", 1403, HttpStatus.BAD_REQUEST),

    // ========== 파일 관련 (1500~1599) ==========
    FILE_UPLOAD_FAILED("파일 업로드에 실패했습니다.", 1500, HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_COUNT_EXCEEDED("이미지는 최대 10개까지 업로드할 수 있습니다.", 1501, HttpStatus.BAD_REQUEST),

    // ========== 수리 관련 (1600~1699) ==========
    REPAIR_NOT_FOUND("수리 정보를 찾을 수 없습니다", 1600, HttpStatus.NOT_FOUND),
    REPAIR_ALREADY_FINALIZED("이미 제출된 수리 정보는 수정할 수 없습니다", 1601, HttpStatus.BAD_REQUEST),
    CANT_FINALIZE_REPAIR_FALSE("최종 확정 요청은 False일 수 없습니다", 1602, HttpStatus.BAD_REQUEST),
    IMAGE_REQUIRED_FOR_REPAIR_FINALIZE("최종 제출을 위해서는 수리 전/후 이미지가 최소 1장씩 필요합니다.", 1603, HttpStatus.BAD_REQUEST),

    // ========== 계약 정보 관련 (1700~1799) ==========
    CONTRACT_NOT_FOUND("계약 정보를 찾을 수 없습니다.", 1700, HttpStatus.NOT_FOUND),
    CONTRACT_TYPE_NOT_FOUND("알 수 없는 계약 유형입니다.", 1701, HttpStatus.BAD_REQUEST);

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
