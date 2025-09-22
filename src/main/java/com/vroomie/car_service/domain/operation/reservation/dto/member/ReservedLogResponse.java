package com.vroomie.car_service.domain.operation.reservation.dto.member;

import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.Builder;
import com.vroomie.car_service.domain.operation.reservation.enums.RentStatus;
import com.vroomie.car_service.domain.operation.reservation.enums.Purpose;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReservedLogResponse {

    private Long id;
    private Long carId;
    private Long reservationId;
    private String carNumber;
    private String carModel;
    @JsonFormat(pattern = "yy-MM-dd HH:mm")
    private LocalDateTime startedAt;
    @JsonFormat(pattern = "yy-MM-dd HH:mm")
    private LocalDateTime endedAt;
    private RentStatus rentStatus; // 대여 상태
    @JsonFormat(pattern = "yy-MM-dd HH:mm")
    private LocalDateTime returnDate;
    @JsonFormat(pattern = "yy-MM-dd HH:mm")
    private LocalDateTime createdAt;
    private Purpose purpose;
    private String detail;
}
