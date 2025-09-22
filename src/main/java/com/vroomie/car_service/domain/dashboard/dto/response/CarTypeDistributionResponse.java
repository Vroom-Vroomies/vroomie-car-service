package com.vroomie.car_service.domain.dashboard.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 차량 타입별 분포 조회 응답 DTO
 * - 차량 종류별 분포 현황(트럭, SUV ...)
 */
@Getter
@Builder
public class CarTypeDistributionResponse {
    private Integer total;
    private String centerLabel;
    private List<CarTypeDistributionItem> items;

    @Getter
    @Builder
    public static class CarTypeDistributionItem {
        private String label;
        private Integer value;
        private String color;
        private Double percentage;
    }
}