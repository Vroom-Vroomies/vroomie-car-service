package com.vroomie.car_service.domain.operation.reservation.dto.member;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import com.vroomie.car_service.domain.operation.reservation.enums.Purpose;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberCarReservationRequest {

    private Long carId;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private Purpose purpose;
    private String detail;
}
