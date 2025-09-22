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
    
    @JsonFormat(pattern = "yy-MM-dd HH:mm")
    private LocalDateTime startedAt;
    
    @JsonFormat(pattern = "yy-MM-dd HH:mm")
    private LocalDateTime endedAt;
    
    private String reservationStatus;
    private String purpose;
}