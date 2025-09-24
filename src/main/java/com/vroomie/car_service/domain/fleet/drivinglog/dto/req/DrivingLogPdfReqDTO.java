package com.vroomie.car_service.domain.fleet.drivinglog.dto.req;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DrivingLogPdfReqDTO {
    // 운행 일지 시작 날짜
    private LocalDate startDate;

    // 운행 일지 종료 날짜
    private LocalDate endDate;
}
