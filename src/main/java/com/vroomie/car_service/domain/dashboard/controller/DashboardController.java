package com.vroomie.car_service.domain.dashboard.controller;

import com.vroomie.car_service.domain.dashboard.service.DashboardService;
import com.vroomie.car_service.domain.dashboard.service.DashboardCostService;
import com.vroomie.car_service.domain.dashboard.service.DynamicFeeTypeService;
import com.vroomie.car_service.domain.dashboard.dto.response.CarTypeDistributionResponse;
import com.vroomie.car_service.domain.dashboard.dto.response.CarStatusResponse;
import com.vroomie.car_service.domain.dashboard.dto.response.OperationalStatsResponse;
import com.vroomie.car_service.domain.dashboard.dto.response.MonthlyOperatingCostResponse;
import com.vroomie.car_service.domain.dashboard.dto.response.VehicleMaintenanceResponse;
import com.vroomie.car_service.domain.dashboard.dto.response.MaintenanceBreakdownResponse;
import com.vroomie.car_service.domain.dashboard.dto.response.FeeTypeResponse;
import com.vroomie.car_service.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "대시 보드", description = "대시 보드 조회 API")
public class DashboardController {

    private final DashboardService dashboardService;
    private final DashboardCostService dashboardCostService;
    private final DynamicFeeTypeService dynamicFeeTypeService;

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

    /**
     * 회사별 월별 운영 비용 조회
     * 고정비용과 변동비용을 포함한 월별 운영 비용 분석 데이터를 제공합니다.
     *
     * @param companyId 회사 ID
     * @param period 조회 기간 (month: 6개월, quarter: 3개월, year: 12개월)
     * @param startDate 조회 시작일 (선택사항, 기본값은 period에 따라 자동 설정)
     * @param endDate 조회 종료일 (선택사항, 기본값은 현재 날짜)
     * @return 월별 운영 비용 분석 응답 데이터
     */
    @Operation(summary = "월별 운영 비용 조회", description = "회사의 월별 운영 비용을 분석하여 제공합니다.")
    @GetMapping("/{companyId}/monthly-costs")
    public ResponseEntity<ApiResponse<MonthlyOperatingCostResponse>> getMonthlyCosts(
            @Parameter(description = "회사 ID", required = true) @PathVariable Long companyId,
            @Parameter(description = "조회 기간") @RequestParam(defaultValue = "month") String period,
            @Parameter(description = "조회 시작일") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "조회 종료일") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("[DashboardController] 월별 운영 비용 조회 시작 - companyId: {}, period: {}", companyId, period);
        MonthlyOperatingCostResponse response = dashboardCostService.getMonthlyCosts(companyId, period, startDate, endDate);
        log.info("[DashboardController] 월별 운영 비용 조회 완료 - companyId: {}, 총 비용: {}", companyId, response.getSummary().getTotalCost());
        return ResponseEntity.ok(ApiResponse.success(response, "월별 운영 비용 조회가 성공적으로 완료되었습니다."));
    }

    /**
     * 회사별 차량 유지보수 비용 조회
     * 특정 차량들의 유지보수 관련 비용을 분석하여 제공합니다.
     *
     * @param companyId 회사 ID
     * @param period 조회 기간
     * @param vehicleIds 조회할 차량 ID 리스트 (선택사항, 미지정시 전체 차량)
     * @param startDate 조회 시작일 (선택사항)
     * @param endDate 조회 종료일 (선택사항)
     * @param limit 조회할 최대 차량 수 (기본값: 8대)
     * @return 차량별 유지보수 비용 분석 응답 데이터
     */
    @Operation(summary = "차량별 유지보수 비용 조회", description = "차량별 유지보수 비용을 분석하여 제공합니다.")
    @GetMapping("/{companyId}/vehicle-maintenance")
    public ResponseEntity<ApiResponse<VehicleMaintenanceResponse>> getVehicleMaintenanceCosts(
            @Parameter(description = "회사 ID", required = true) @PathVariable Long companyId,
            @Parameter(description = "조회 기간") @RequestParam(defaultValue = "month") String period,
            @Parameter(description = "조회할 차량 ID 리스트") @RequestParam(required = false) List<String> vehicleIds,
            @Parameter(description = "조회 시작일") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "조회 종료일") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "조회할 최대 차량 수") @RequestParam(defaultValue = "8") int limit) {

        log.info("[DashboardController] 차량별 유지보수 비용 조회 시작 - companyId: {}, limit: {}", companyId, limit);
        VehicleMaintenanceResponse response = dashboardCostService.getVehicleMaintenanceCosts(
            companyId, period, vehicleIds, startDate, endDate, limit);
        log.info("[DashboardController] 차량별 유지보수 비용 조회 완료 - companyId: {}, 분석 차량수: {}",
            companyId, response.getSummary().getTotalVehicles());
        return ResponseEntity.ok(ApiResponse.success(response, "차량별 유지보수 비용 조회가 성공적으로 완료되었습니다."));
    }

    /**
     * 회사별 유지보수 비용 분석 조회
     * 유지보수 관련 비용을 비용 유형별로 분석하여 제공합니다.
     *
     * @param companyId 회사 ID
     * @param period 조회 기간
     * @param month 분석할 특정 월 (YYYY-MM 형식, 선택사항)
     * @return 유지보수 비용 분석 응답 데이터
     */
    @Operation(summary = "유지보수 비용 분석 조회", description = "유지보수 비용을 유형별로 분석하여 제공합니다.")
    @GetMapping("/{companyId}/maintenance-breakdown")
    public ResponseEntity<ApiResponse<MaintenanceBreakdownResponse>> getMaintenanceBreakdown(
            @Parameter(description = "회사 ID", required = true) @PathVariable Long companyId,
            @Parameter(description = "조회 기간") @RequestParam(defaultValue = "month") String period,
            @Parameter(description = "분석할 특정 월 (YYYY-MM)") @RequestParam(required = false) String month) {

        log.info("[DashboardController] 유지보수 비용 분석 조회 시작 - companyId: {}, period: {}", companyId, period);
        MaintenanceBreakdownResponse response = dashboardCostService.getMaintenanceBreakdown(companyId, period, month);
        log.info("[DashboardController] 유지보수 비용 분석 조회 완료 - companyId: {}, 총 비용: {}", companyId, response.getTotal());
        return ResponseEntity.ok(ApiResponse.success(response, "유지보수 비용 분석 조회가 성공적으로 완료되었습니다."));
    }

    /**
     * 회사별 사용 가능한 비용 유형 조회
     * 실제 데이터에서 발견된 비용 유형들의 메타데이터를 제공합니다.
     *
     * @param companyId 회사 ID
     * @return 사용 가능한 비용 유형 응답 데이터
     */
    @Operation(summary = "사용 가능한 비용 유형 조회", description = "실제 데이터에서 발견된 비용 유형들을 제공합니다.")
    @GetMapping("/company/{companyId}/fee-types")
    public ResponseEntity<ApiResponse<FeeTypeResponse>> getFeeTypes(
            @Parameter(description = "회사 ID", required = true) @PathVariable Long companyId) {

        log.info("[DashboardController] 비용 유형 조회 시작 - companyId: {}", companyId);
        FeeTypeResponse response = dynamicFeeTypeService.getAvailableFeeTypes(companyId);
        log.info("[DashboardController] 비용 유형 조회 완료 - companyId: {}, 발견된 유형수: {}",
            companyId, response.getStatistics().getTotalFeeTypes());
        return ResponseEntity.ok(ApiResponse.success(response, "비용 유형 조회가 성공적으로 완료되었습니다."));
    }
}