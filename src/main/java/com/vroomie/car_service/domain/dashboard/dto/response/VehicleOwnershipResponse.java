package com.vroomie.car_service.domain.dashboard.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 차량 보유 현황 조회 응답 DTO
 * - 차량 종류 별 보유 현황(트럭, SUV ...)
 */
@Getter
@Builder
public class VehicleOwnershipResponse {
    private Integer total;
    private String centerLabel;
    private List<VehicleOwnershipItem> items;

    @Getter
    @Builder
    public static class VehicleOwnershipItem {
        private String label;
        private Integer value;
        private String color;
        private Double percentage;
    }
}