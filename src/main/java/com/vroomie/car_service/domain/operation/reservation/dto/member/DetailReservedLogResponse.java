package com.vroomie.car_service.domain.operation.reservation.dto.member;

import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import com.vroomie.car_service.domain.operation.reservation.enums.Purpose;
import com.vroomie.car_service.domain.operation.reservation.enums.RentStatus;
import com.vroomie.car_service.domain.operation.reservation.enums.ReservationStatus;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DetailReservedLogResponse {

    private Long reservationLogId;

    private Long carId;
    private String carNumber;
    private String carModel;
    private String carImage;
    private String carType;
    private String carFuelType;
    private String carAllowableCapacity;
    private String carGearType;
    private String carUsageType;
    private String carStatus; // 차량 상태

    @JsonFormat(pattern = "yy-MM-dd HH:mm")
    private LocalDateTime startedAt;
    @JsonFormat(pattern = "yy-MM-dd HH:mm")
    private LocalDateTime endedAt;
    private Purpose purpose;
    private String detail;
    private RentStatus rentStatus;
    private ReservationStatus reservationStatus;
}
