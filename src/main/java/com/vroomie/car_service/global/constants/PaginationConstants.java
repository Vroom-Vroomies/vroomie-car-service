package com.vroomie.car_service.global.constants;

/**
 * 페이지네이션 관련 전역 상수 클래스
 */
public final class PaginationConstants {

    private PaginationConstants() {
        // 인스턴스화 방지
    }

    // 페이지네이션 기본값
    public static final String DEFAULT_PAGE_NUMBER = "1";
    public static final String DEFAULT_PAGE_SIZE = "10";
    
    // 페이지네이션 제한값
    public static final int MIN_PAGE_NUMBER = 1;
    public static final int MIN_PAGE_SIZE = 1;
    public static final int MAX_PAGE_SIZE = 100;
    
    // 페이지네이션 계산용 상수
    public static final int PAGE_OFFSET = 1;
    public static final int FIRST_ITEM_INDEX = 0;
    public static final int MIN_COUNT_THRESHOLD = 0;
}