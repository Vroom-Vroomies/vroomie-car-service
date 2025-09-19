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
public class MemberReservationTimeResponse {

    private Long reservationId;
    private String carNumber;
    private String carModel;
    
    @JsonFormat(pattern = "yyyy년 MM월 dd일 HH시 mm분")
    private LocalDateTime startedAt;
    
    @JsonFormat(pattern = "yyyy년 MM월 dd일 HH시 mm분")
    private LocalDateTime endedAt;
    
    private String reservationStatus;
    private String purpose;
}