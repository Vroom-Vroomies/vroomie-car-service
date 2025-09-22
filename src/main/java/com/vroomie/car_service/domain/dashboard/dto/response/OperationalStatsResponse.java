package com.vroomie.car_service.domain.dashboard.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 *  운영 통계 조회 응답 DTO
 *  - 대시보드 운영 통계(운행 기록 미작성, 지급 된차량, 대여 중차량, 반납 지연 차량)
 */
@Getter
@Builder
public class OperationalStatsResponse {
    private List<OperationalStatsItem> items;

    @Getter
    @Builder
    public static class OperationalStatsItem {
        private String label;
        private Integer value;
        private String unit; // 단위('대')
    }
}