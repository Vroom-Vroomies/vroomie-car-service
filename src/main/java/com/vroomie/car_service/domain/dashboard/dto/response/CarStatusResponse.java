package com.vroomie.car_service.domain.dashboard.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
/**
 * 차량 상태 분포 조회 응답 DTO
 * - 차량 상태(정상, 점검중, 수리중, 보험 만료), 차량 수, 색, 비율
 */
@Getter
@Builder
public class CarStatusResponse {
    private Integer total;
    private String centerLabel; // donut chart 중앙에 올 라벨
    private List<CarStatusItem> items;

    @Getter
    @Builder
    public static class CarStatusItem {
        private String status;
        private Integer count;
        private String color;
        private Double percentage;
    }
}