package com.vroomie.car_service.domain.dashboard.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * TCO 분석 요청 DTO
 */
@Getter
@Builder
@Jacksonized
public class TcoAnalysisRequest {

    /**
     * 회사 ID
     */
    @NotNull(message = "회사 ID는 필수입니다")
    private Long companyId;

    /**
     * 분석 연도 (0이면 전체)
     */
    @Min(value = 0, message = "연도는 0 이상이어야 합니다")
    @Max(value = 2100, message = "연도는 2100 이하여야 합니다")
    private Integer year;

    /**
     * 분석 월 (0이면 전체)
     */
    @Min(value = 0, message = "월은 0 이상이어야 합니다")
    @Max(value = 12, message = "월은 12 이하여야 합니다")
    private Integer month;

    /**
     * 페이지 번호 (기본값: 0)
     */
    @Min(value = 0, message = "페이지 번호는 0 이상이어야 합니다")
    @Builder.Default
    private Integer page = 0;

    /**
     * 페이지 크기 (기본값: 20)
     */
    @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다")
    @Max(value = 100, message = "페이지 크기는 100 이하여야 합니다")
    @Builder.Default
    private Integer size = 20;

    /**
     * 정렬 방향 (기본값: totalCost,desc)
     */
    @Builder.Default
    private String sort = "totalCost,desc";
}