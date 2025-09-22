package com.vroomie.car_service.domain.fleet.drivinglog.dto.req;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LocationReqDTO {
    private BigDecimal longitude; // 경도 : x값
    private BigDecimal latitude; // 위도 : y값
}
