package com.vroomie.car_service.domain.dashboard.controller;

import com.vroomie.car_service.domain.dashboard.service.DashboardService;
import com.vroomie.car_service.domain.dashboard.dto.response.CarTypeDistributionResponse;
import com.vroomie.car_service.domain.dashboard.dto.response.CarStatusResponse;
import com.vroomie.car_service.domain.dashboard.dto.response.OperationalStatsResponse;
import com.vroomie.car_service.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "대시 보드", description = "대시 보드 조회 API")
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * 회사별 차량 타입별 분포 현황 조회
     * @param companyId 회사 ID
     * @return ApiResponse<CarTypeDistributionResponse> 차량 타입별 분포 응답
     */
    @GetMapping("/cars/{companyId}/type-distribution")
    public ResponseEntity<ApiResponse<CarTypeDistributionResponse>> getCarTypeDistribution(@PathVariable Long companyId) {
        log.info("[DashboardController] 차량 타입별 분포 조회 시작 - companyId: {}", companyId);
        CarTypeDistributionResponse response = dashboardService.getCarTypeDistribution(companyId);
        log.info("[DashboardController] 차량 타입별 분포 조회 완료 - companyId: {}, 총 차량수: {}", companyId, response.getTotal());
        return ResponseEntity.ok(ApiResponse.success(response, "차량 타입별 분포 조회가 성공적으로 완료되었습니다."));
    }

    /**
     * 회사별 차량 상태 분포 조회
     * @param companyId 회사 ID
     * @return ApiResponse<CarStatusResponse> 차량 상태 분포 응답
     */
    @GetMapping("/cars/{companyId}/status-distribution")
    public ResponseEntity<ApiResponse<CarStatusResponse>> getCarStatus(@PathVariable Long companyId) {
        log.info("[DashboardController] 차량 상태 분포 조회 시작 - companyId: {}", companyId);
        CarStatusResponse response = dashboardService.getCarStatus(companyId);
        log.info("[DashboardController] 차량 상태 분포 조회 완료 - companyId: {}, 정상 차량수: {}", companyId, response.getTotal());
        return ResponseEntity.ok(ApiResponse.success(response, "차량 상태 분포 조회가 성공적으로 완료되었습니다."));
    }

    /**
     * 회사별 운영 통계 조회
     * @param companyId 회사 ID
     * @return ApiResponse<OperationalStatsResponse> 운영 통계 응답
     */
    @GetMapping("/cars/{companyId}/operational-stats")
    public ResponseEntity<ApiResponse<OperationalStatsResponse>> getOperationalStats(@PathVariable Long companyId) {
        log.info("[DashboardController] 운영 통계 조회 시작 - companyId: {}", companyId);
        OperationalStatsResponse response = dashboardService.getOperationalStats(companyId);
        log.info("[DashboardController] 운영 통계 조회 완료 - companyId: {}", companyId);
        return ResponseEntity.ok(ApiResponse.success(response, "운영 통계 조회가 성공적으로 완료되었습니다."));
    }
}