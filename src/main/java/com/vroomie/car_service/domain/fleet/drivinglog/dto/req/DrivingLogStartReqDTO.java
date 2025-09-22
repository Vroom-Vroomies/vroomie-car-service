package com.vroomie.car_service.domain.fleet.drivinglog.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DrivingLogStartReqDTO {

    @NotNull(message = "시작 주행 거리계(km)는 필수입니다.")
    private Long startOdometer;

    @NotBlank(message = "주행 전 계기판 사진은 필수입니다.")
    private String startOdometerImage;

    @NotNull(message = "운행 시작 시간 선택은 필수입니다.")
    private LocalDateTime startedAt;

    // 프론트에서 GPS API로 수집
    private BigDecimal startLat;
    private BigDecimal startLng;

    // 프론트에서 역지오코딩으로 수집
    private String startLocation;

}
