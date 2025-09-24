package com.vroomie.car_service.global.constants;

/**
 * 비즈니스 로직 관련 전역 상수 클래스
 */
public final class BusinessConstants {

    private BusinessConstants() {
        // 인스턴스화 방지
    }

    // 업무 시간 관련 상수 (24시간 운영)
    public static final int BUSINESS_START_HOUR = 0;
    public static final int BUSINESS_END_HOUR = 24;
}