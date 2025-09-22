package com.vroomie.car_service.domain.fleet.drivinglog.dto.req;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DrivingLogEndReqDTO {

    @NotNull(message = "종료 주행 거리계(km)는 필수입니다.")
    private Long endOdometer;

    @NotNull(message = "주행 후 계기판 사진은 필수입니다.")
    private String endOdometerImage;

    @NotNull(message = "운행 종료 시간 선택은 필수입니다.")
    private LocalDateTime endedAt;

    // 프론트에서 GPS API로 수집
    private BigDecimal endLat;
    private BigDecimal endLng;

    // 프론트에서 역지오코딩으로 수집
    private String endLocation;

}
