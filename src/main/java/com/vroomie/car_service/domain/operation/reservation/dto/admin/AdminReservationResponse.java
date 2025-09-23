package com.vroomie.car_service.domain.operation.reservation.dto.admin;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import com.vroomie.car_service.domain.operation.reservation.enums.ReservationStatus;
import com.vroomie.car_service.domain.operation.reservation.enums.RentStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.vroomie.car_service.domain.operation.reservation.enums.Purpose;
// import com.vroomie.car_service.domain.operation.reservation.enums.DrivingLogStatus;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdminReservationResponse {

    private Long id;
    private String carNumber;
    private String memberName; // 대여 신청자 이름

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime startedAt; // 대여 시작일자

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime endedAt; // 대여 종료일자

    private ReservationStatus reservationStatus; // 예약 상태(승인 여부)
    private RentStatus rentStatus; // 대여 상태(대여 중, 반납 완료)
    // private DrivingLogStatus drivingLogStatus; // 운행 로그 상태(준비중, 작성중, 대기중, 완료)
    private String drivingLogStatus; // 운행 로그 상태(준비중, 작성중, 대기중, 완료)
    private String adminName; // 담당자 이름
    private Purpose purpose;
    private String detail;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp createdAt;
}
