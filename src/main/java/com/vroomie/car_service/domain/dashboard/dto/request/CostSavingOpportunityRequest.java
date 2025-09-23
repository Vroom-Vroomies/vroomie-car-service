package com.vroomie.car_service.domain.dashboard.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * 비용 절감 기회 분석 요청 DTO
 */
@Getter
@Builder
@Jacksonized
public class CostSavingOpportunityRequest {

    /**
     * 회사 ID
     */
    @NotNull(message = "회사 ID는 필수입니다")
    private Long companyId;

    /**
     * 분석 시작일
     */
    @NotNull(message = "분석 시작일은 필수입니다")
    private LocalDate startDate;

    /**
     * 분석 종료일
     */
    @NotNull(message = "분석 종료일은 필수입니다")
    private LocalDate endDate;

    /**
     * 카테고리 필터 (선택사항)
     */
    private String category;
}