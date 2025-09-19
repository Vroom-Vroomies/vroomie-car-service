package com.vroomie.car_service.domain.operation.reservation.dto.member;

import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import com.vroomie.car_service.domain.operation.reservation.enums.RentStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReservedLogResponse {

    private Long id;
    private String carNumber;
    @JsonFormat(pattern = "yyyy년 MM월 dd일 HH시 mm분")
    private LocalDateTime startedAt;
    @JsonFormat(pattern = "yyyy년 MM월 dd일 HH시 mm분")
    private LocalDateTime endedAt;
    private RentStatus rentStatus; // 대여 상태
}
