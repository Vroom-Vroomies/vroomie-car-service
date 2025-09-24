package com.vroomie.car_service.domain.dashboard.controller;

import com.vroomie.car_service.domain.dashboard.dto.response.*;
import com.vroomie.car_service.domain.dashboard.service.DashboardService;
import com.vroomie.car_service.domain.dashboard.service.DashboardCostService;
import com.vroomie.car_service.domain.dashboard.service.DynamicFeeTypeService;
import com.vroomie.car_service.domain.dashboard.service.TcoAnalysisService;
import com.vroomie.car_service.domain.dashboard.service.VehicleEfficiencyService;
import com.vroomie.car_service.domain.dashboard.service.CostSavingOpportunityService;
import com.vroomie.car_service.domain.dashboard.dto.request.TcoAnalysisRequest;
import com.vroomie.car_service.domain.dashboard.dto.request.VehicleEfficiencyRequest;
import com.vroomie.car_service.domain.dashboard.dto.request.CostSavingOpportunityRequest;
import com.vroomie.car_service.domain.dashboard.entity.VehicleEfficiencyEntity;
import com.vroomie.car_service.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "대시 보드", description = "대시 보드 조회 API")
public class DashboardController {

    private final DashboardService dashboardService;
    private final DashboardCostService dashboardCostService;
    private final DynamicFeeTypeService dynamicFeeTypeService;
    private final TcoAnalysisService tcoAnalysisService;
    private final VehicleEfficiencyService vehicleEfficiencyService;
    private final CostSavingOpportunityService costSavingOpportunityService;

    /**
     * 전체 대시보드 정보 조회
     */
    @GetMapping("/{companyId}/summary")
    public ResponseEntity<ApiResponse<DashboardSummaryResponse>> getDashboardSummary(@PathVariable Long companyId){
        log.info("[DashboardController] 대시보드 요약 조회 시작 - companyId: {}", companyId);

        // 1. 차량 소유 현황 (차량 타입별 분포)
        CarTypeDistributionResponse carTypeData = dashboardService.getCarTypeDistribution(companyId);
        DashboardSummaryResponse.VehicleOwnership vehicleOwnership = DashboardSummaryResponse.VehicleOwnership.builder()
            .total(carTypeData.getTotal())
            .centerLabel(carTypeData.getCenterLabel())
            .items(carTypeData.getItems().stream()
                .map(item -> DashboardSummaryResponse.VehicleOwnershipItem.builder()
                    .label(item.getLabel())
                    .value(item.getValue())
                    .color(item.getColor())
                    .percentage(item.getPercentage().intValue())
                    .build())
                .collect(Collectors.toList()))
            .build();

        // 2. 차량 상태 현황
        CarStatusResponse carStatusData = dashboardService.getCarStatus(companyId);
        DashboardSummaryResponse.VehicleStatus vehicleStatus = DashboardSummaryResponse.VehicleStatus.builder()
            .total(carStatusData.getTotal())
            .centerLabel(carStatusData.getCenterLabel())
            .items(carStatusData.getItems().stream()
                .map(item -> DashboardSummaryResponse.VehicleStatusItem.builder()
                    .status(item.getStatus())
                    .count(item.getCount())
                    .color(item.getColor())
                    .percentage(item.getPercentage().intValue())
                    .build())
                .collect(Collectors.toList()))
            .build();

        // 3. 운영 통계
        OperationalStatsResponse operationalStatsData = dashboardService.getOperationalStats(companyId);
        DashboardSummaryResponse.OperationalStats operationalStats = DashboardSummaryResponse.OperationalStats.builder()
            .items(operationalStatsData.getItems().stream()
                .map(item -> DashboardSummaryResponse.OperationalStatsItem.builder()
                    .label(item.getLabel())
                    .value(item.getValue())
                    .unit(item.getUnit())
                    .build())
                .collect(Collectors.toList()))
            .build();

        // 4. 월별 운영 비용 (최근 6개월)
        MonthlyOperatingCostResponse monthlyCostData = dashboardCostService.getMonthlyCosts(companyId, "month", null, null);
        List<DashboardSummaryResponse.MonthlyOperatingCost> monthlyOperatingCost = monthlyCostData.getMonthlyData().stream()
            .map(monthData -> DashboardSummaryResponse.MonthlyOperatingCost.builder()
                .month(monthData.getMonth())
                .fixedCost(monthData.getFixedCost())
                .variableCost(monthData.getVariableCost().stream()
                    .map(varItem -> DashboardSummaryResponse.VariableCostItem.builder()
                        .feeTypeId(varItem.getFeeTypeId())
                        .amount(varItem.getAmount())
                        .color(varItem.getColor())
                        .build())
                    .collect(Collectors.toList()))
                .total(monthData.getTotal())
                .previousMonthDiff(monthData.getPreviousMonthDiff())
                .build())
            .collect(Collectors.toList());

        // 5. 유지보수 비용 분석
        MaintenanceBreakdownResponse maintenanceData = dashboardCostService.getMaintenanceBreakdown(companyId, "month", null);
        DashboardSummaryResponse.MaintenanceCost maintenanceCost = DashboardSummaryResponse.MaintenanceCost.builder()
            .total(maintenanceData.getTotal())
            .breakdown(maintenanceData.getBreakdown().stream()
                .map(item -> DashboardSummaryResponse.MaintenanceBreakdownItem.builder()
                    .feeTypeId(item.getFeeTypeId())
                    .amount(item.getAmount())
                    .percentage(item.getPercentage().intValue())
                    .color(item.getColor())
                    .build())
                .collect(Collectors.toList()))
            .build();

        // 6. 비효율성 데이터 (상위 5대)
        VehicleEfficiencyRequest efficiencyRequest = VehicleEfficiencyRequest.builder()
            .companyId(companyId)
            .page(0)
            .size(5)
            .build();
        VehicleEfficiencyResponse efficiencyData = vehicleEfficiencyService.getVehicleEfficiencyAnalysis(efficiencyRequest);
        List<DashboardSummaryResponse.InefficiencyData> inefficiencyData = new ArrayList<>();

        for (int i = 0; i < efficiencyData.getVehicles().size(); i++) {
            VehicleEfficiencyResponse.VehicleEfficiencyData vehicle = efficiencyData.getVehicles().get(i);
            DashboardSummaryResponse.InefficiencyData data = DashboardSummaryResponse.InefficiencyData.builder()
                .vehicleId(vehicle.getVehicleId())
                .vehicleName(vehicle.getVehicleName())
                .inefficiencyScore(vehicle.getInefficiencyScore().intValue())
                .rank(i + 1)
                .period("month")
                .year(LocalDate.now().getYear())
                .month(LocalDate.now().getMonthValue())
                .color("#8B5CF6")
                .build();
            inefficiencyData.add(data);
        }

        DashboardSummaryResponse response = DashboardSummaryResponse.builder()
            .vehicleOwnership(vehicleOwnership)
            .vehicleStatus(vehicleStatus)
            .operationalStats(operationalStats)
            .monthlyOperatingCost(monthlyOperatingCost)
            .maintenanceCost(maintenanceCost)
            .inefficiencyData(inefficiencyData)
            .build();

        log.info("[DashboardController] 대시보드 요약 조회 완료 - companyId: {}", companyId);
        return ResponseEntity.ok(ApiResponse.success(response,"대시보드 조회가 성공적으로 완료되었습니다."));
    }

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

    /**
     * TCO (Total Cost of Ownership) 분석 조회 (GET)
     * 차량별 총 소유비용을 분석하여 제공합니다.
     *
     * @param companyId 회사 ID
     * @param year 분석 연도 (선택사항, 기본값: 현재 연도)
     * @param month 분석 월 (선택사항, 0이면 전체)
     * @param page 페이지 번호 (기본값: 0)
     * @param size 페이지 크기 (기본값: 20)
     * @param sort 정렬 방향 (기본값: totalCost,desc)
     * @return TCO 분석 응답 데이터
     */
    @Operation(summary = "TCO 분석 조회 (GET)", description = "차량별 총 소유비용을 분석하여 제공합니다.")
    @GetMapping("/{companyId}/tco-analysis")
    public ResponseEntity<ApiResponse<TcoAnalysisResponse>> getTcoAnalysisGet(
            @Parameter(description = "회사 ID", required = true) @PathVariable Long companyId,
            @Parameter(description = "분석 연도") @RequestParam(required = false) Integer year,
            @Parameter(description = "분석 월") @RequestParam(required = false) Integer month,
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") Integer size,
            @Parameter(description = "정렬 방향") @RequestParam(defaultValue = "totalCost,desc") String sort) {

        log.info("[DashboardController] TCO 분석 조회 (GET) 시작 - companyId: {}", companyId);

        TcoAnalysisRequest request = TcoAnalysisRequest.builder()
            .companyId(companyId)
            .year(year != null ? year : LocalDate.now().getYear())
            .month(month != null ? month : 0)
            .page(page)
            .size(size)
            .sort(sort)
            .build();

        TcoAnalysisResponse response = tcoAnalysisService.getTcoAnalysis(request);
        log.info("[DashboardController] TCO 분석 조회 (GET) 완료 - companyId: {}, 분석 차량수: {}",
            companyId, response.getPageInfo().getTotalElements());
        return ResponseEntity.ok(ApiResponse.success(response, "TCO 분석 조회가 성공적으로 완료되었습니다."));
    }

    /**
     * TCO (Total Cost of Ownership) 분석 조회 (POST)
     * 차량별 총 소유비용을 분석하여 제공합니다.
     *
     * @param request TCO 분석 요청 데이터
     * @return TCO 분석 응답 데이터
     */
    @Operation(summary = "TCO 분석 조회 (POST)", description = "차량별 총 소유비용을 분석하여 제공합니다.")
    @PostMapping("/tco-analysis")
    public ResponseEntity<ApiResponse<TcoAnalysisResponse>> getTcoAnalysis(
            @Valid @RequestBody TcoAnalysisRequest request) {

        log.info("[DashboardController] TCO 분석 조회 (POST) 시작 - companyId: {}", request.getCompanyId());
        TcoAnalysisResponse response = tcoAnalysisService.getTcoAnalysis(request);
        log.info("[DashboardController] TCO 분석 조회 (POST) 완료 - companyId: {}, 분석 차량수: {}",
            request.getCompanyId(), response.getPageInfo().getTotalElements());
        return ResponseEntity.ok(ApiResponse.success(response, "TCO 분석 조회가 성공적으로 완료되었습니다."));
    }

    /**
     * 차량 효율성 분석 조회 (GET)
     * 차량별 효율성 메트릭과 순위를 제공합니다.
     *
     * @param companyId 회사 ID
     * @param vehicleId 차량 ID (선택사항)
     * @param period 분석 기간 유형 (선택사항)
     * @param year 분석 연도 (선택사항, 기본값: 현재 연도)
     * @param month 분석 월 (선택사항, 0이면 전체)
     * @param page 페이지 번호 (기본값: 0)
     * @param size 페이지 크기 (기본값: 20)
     * @return 차량 효율성 분석 응답 데이터
     */
    @Operation(summary = "차량 효율성 분석 조회 (GET)", description = "차량별 효율성 메트릭과 순위를 제공합니다.")
    @GetMapping("/{companyId}/vehicle-efficiency")
    public ResponseEntity<ApiResponse<VehicleEfficiencyResponse>> getVehicleEfficiencyGet(
            @Parameter(description = "회사 ID", required = true) @PathVariable Long companyId,
            @Parameter(description = "차량 ID") @RequestParam(required = false) String vehicleId,
            @Parameter(description = "분석 기간") @RequestParam(required = false) String period,
            @Parameter(description = "분석 연도") @RequestParam(required = false) Integer year,
            @Parameter(description = "분석 월") @RequestParam(required = false) Integer month,
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") Integer size) {

        log.info("[DashboardController] 차량 효율성 분석 조회 (GET) 시작 - companyId: {}", companyId);

        VehicleEfficiencyEntity.AnalysisPeriod analysisPeriod = null;
        if (period != null) {
            try {
                analysisPeriod = VehicleEfficiencyEntity.AnalysisPeriod.valueOf(period.toUpperCase());
            } catch (IllegalArgumentException e) {
                analysisPeriod = VehicleEfficiencyEntity.AnalysisPeriod.MONTH;
            }
        }

        VehicleEfficiencyRequest request = VehicleEfficiencyRequest.builder()
            .companyId(companyId)
            .vehicleId(vehicleId)
            .period(analysisPeriod)
            .year(year != null ? year : LocalDate.now().getYear())
            .month(month != null ? month : 0)
            .page(page)
            .size(size)
            .build();

        VehicleEfficiencyResponse response = vehicleEfficiencyService.getVehicleEfficiencyAnalysis(request);
        log.info("[DashboardController] 차량 효율성 분석 조회 (GET) 완료 - companyId: {}, 분석 차량수: {}",
            companyId, response.getPageInfo().getTotalElements());
        return ResponseEntity.ok(ApiResponse.success(response, "차량 효율성 분석 조회가 성공적으로 완료되었습니다."));
    }

    /**
     * 차량 효율성 분석 조회 (POST)
     * 차량별 효율성 메트릭과 순위를 제공합니다.
     *
     * @param request 차량 효율성 분석 요청 데이터
     * @return 차량 효율성 분석 응답 데이터
     */
    @Operation(summary = "차량 효율성 분석 조회 (POST)", description = "차량별 효율성 메트릭과 순위를 제공합니다.")
    @PostMapping("/vehicle-efficiency")
    public ResponseEntity<ApiResponse<VehicleEfficiencyResponse>> getVehicleEfficiency(
            @Valid @RequestBody VehicleEfficiencyRequest request) {

        log.info("[DashboardController] 차량 효율성 분석 조회 (POST) 시작 - companyId: {}", request.getCompanyId());
        VehicleEfficiencyResponse response = vehicleEfficiencyService.getVehicleEfficiencyAnalysis(request);
        log.info("[DashboardController] 차량 효율성 분석 조회 (POST) 완료 - companyId: {}, 분석 차량수: {}",
            request.getCompanyId(), response.getPageInfo().getTotalElements());
        return ResponseEntity.ok(ApiResponse.success(response, "차량 효율성 분석 조회가 성공적으로 완료되었습니다."));
    }

    /**
     * 비용 절감 기회 분석 조회
     * 비용 절감 가능한 영역과 권장사항을 제공합니다.
     *
     * @param request 비용 절감 기회 분석 요청 데이터
     * @return 비용 절감 기회 분석 응답 데이터
     */
    @Operation(summary = "비용 절감 기회 분석 조회", description = "비용 절감 가능한 영역과 권장사항을 제공합니다.")
    @PostMapping("/cost-saving-opportunities")
    public ResponseEntity<ApiResponse<CostSavingOpportunityResponse>> getCostSavingOpportunities(
            @Valid @RequestBody CostSavingOpportunityRequest request) {

        log.info("[DashboardController] 비용 절감 기회 분석 조회 시작 - companyId: {}", request.getCompanyId());
        CostSavingOpportunityResponse response = costSavingOpportunityService.getCostSavingOpportunities(
            request.getCompanyId(), request.getStartDate(), request.getEndDate());
        log.info("[DashboardController] 비용 절감 기회 분석 조회 완료 - companyId: {}, 발견된 기회수: {}",
            request.getCompanyId(), response.getOpportunities().size());
        return ResponseEntity.ok(ApiResponse.success(response, "비용 절감 기회 분석 조회가 성공적으로 완료되었습니다."));
    }

}