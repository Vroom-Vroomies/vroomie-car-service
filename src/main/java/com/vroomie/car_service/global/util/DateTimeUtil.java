package com.vroomie.car_service.global.util;

import java.time.LocalDateTime;
import com.vroomie.car_service.domain.operation.reservation.exception.AdminReservationException;

 
// 날짜/시간 관련 유틸리티 클래스
public class DateTimeUtil {

    /**
     * 문자열을 LocalDateTime으로 파싱합니다.
     * 
     * @param dateTimeStr "2024-01-15T09:00:00" 형식의 문자열
     * @return 파싱된 LocalDateTime 객체
     * @throws AdminReservationException 파싱 실패 시
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        try {
            // "2024-01-15T09:00:00" 형식으로 파싱
            return LocalDateTime.parse(dateTimeStr);
        } catch (Exception e) {
            throw AdminReservationException.invalidDateTimeFormat();
        }
    }

    /**
     * 주어진 시간이 업무시간 내인지 검증합니다.
     * 
     * @param dateTime 검증할 시간
     * @return 업무시간 내이면 true, 아니면 false
     */
    public static boolean isValidBusinessHour(LocalDateTime dateTime) {
        int hour = dateTime.getHour();
        int minute = dateTime.getMinute();

        // 9:00 ~ 18:00, 30분 단위만 허용
        // 18시인 경우 정각(00분)만 허용 (18:00까지만)
        if (hour == 18) {
            return minute == 0;
        }
        
        return hour >= 9 && hour < 18 && (minute == 0 || minute == 30);
    }
}
