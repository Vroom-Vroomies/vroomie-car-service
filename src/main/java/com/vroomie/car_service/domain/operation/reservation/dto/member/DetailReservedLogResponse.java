package com.vroomie.car_service.domain.operation.reservation.dto.member;

import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DetailReservedLogResponse {

    private Long id;

    private String carNumber;
    private String carModel;
    private String carImage;
    private String carType;
    private String carFuelType;
    private String carAllowableCapacity;
    private String carGearType;
    private String carUsageType;
    private String carStatus; // 차량 상태

    @JsonFormat(pattern = "yyyy년 MM월 dd일 HH시 mm분")
    private LocalDateTime startedAt;
    @JsonFormat(pattern = "yyyy년 MM월 dd일 HH시 mm분")
    private LocalDateTime endedAt;
    private String purpose;
}
