package com.vroomie.car_service.domain.dashboard.service;

import com.vroomie.car_service.domain.dashboard.dto.request.TcoAnalysisRequest;
import com.vroomie.car_service.domain.dashboard.dto.response.TcoAnalysisResponse;
import com.vroomie.car_service.domain.dashboard.projection.TcoProjection;
import com.vroomie.car_service.domain.dashboard.repository.TcoAnalysisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TcoAnalysisService {

    private final TcoAnalysisRepository tcoAnalysisRepository;

    public TcoAnalysisResponse getTcoAnalysis(TcoAnalysisRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());

        // For now, use default date range and filters since request doesn't have these fields
        LocalDate startDate = LocalDate.now().minusMonths(1);
        LocalDate endDate = LocalDate.now();

        // Use existing repository method instead of the new one I added
        Page<TcoProjection> vehicleDataPage = tcoAnalysisRepository.findTcoAnalysisByCompanyWithPagination(
            request.getCompanyId(),
            request.getYear() != null ? request.getYear() : 0,
            request.getMonth() != null ? request.getMonth() : 0,
            pageable
        );

        List<TcoAnalysisResponse.TcoVehicleData> vehicleData = vehicleDataPage.getContent().stream()
            .map(this::mapToTcoVehicleData)
            .collect(Collectors.toList());

        TcoAnalysisResponse.TcoSummary summary = calculateTcoSummary(
            request.getCompanyId(),
            startDate,
            endDate
        );

        // TODO: Implement trend and cost distribution if needed
        // List<TcoAnalysisResponse.TrendData> trendData = getTrendData(...);
        // List<TcoAnalysisResponse.CostDistribution> costDistribution = getCostDistribution(...);

        TcoAnalysisResponse.PageInfo pageInfo = TcoAnalysisResponse.PageInfo.builder()
            .totalElements(vehicleDataPage.getTotalElements())
            .totalPages(vehicleDataPage.getTotalPages())
            .currentPage(vehicleDataPage.getNumber())
            .pageSize(vehicleDataPage.getSize())
            .isFirst(vehicleDataPage.isFirst())
            .isLast(vehicleDataPage.isLast())
            .build();

        return TcoAnalysisResponse.builder()
            .summary(summary)
            .vehicles(vehicleData)
            .pageInfo(pageInfo)
            .build();
    }

    private TcoAnalysisResponse.TcoVehicleData mapToTcoVehicleData(TcoProjection projection) {
        BigDecimal totalCost = projection.getTotalCost();

        // Create cost breakdown using existing projection methods
        TcoAnalysisResponse.CostBreakdown costBreakdown = TcoAnalysisResponse.CostBreakdown.builder()
            .acquisitionPercentage(totalCost.compareTo(BigDecimal.ZERO) > 0
                ? projection.getAcquisitionCost().divide(totalCost, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue()
                : 0.0)
            .operationPercentage(totalCost.compareTo(BigDecimal.ZERO) > 0
                ? projection.getOperationCost().divide(totalCost, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue()
                : 0.0)
            .maintenancePercentage(totalCost.compareTo(BigDecimal.ZERO) > 0
                ? projection.getMaintenanceCost().divide(totalCost, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue()
                : 0.0)
            .disposalPercentage(totalCost.compareTo(BigDecimal.ZERO) > 0
                ? projection.getDisposalCost().divide(totalCost, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue()
                : 0.0)
            .build();

        return TcoAnalysisResponse.TcoVehicleData.builder()
            .vehicleId(projection.getVehicleId())
            .vehicleName(projection.getVehicleName())
            .acquisitionCost(projection.getAcquisitionCost())
            .operationCost(projection.getOperationCost())
            .maintenanceCost(projection.getMaintenanceCost())
            .disposalCost(projection.getDisposalCost())
            .totalCost(totalCost)
            .costBreakdown(costBreakdown)
            .year(projection.getYear())
            .month(projection.getMonth())
            .build();
    }

    private TcoAnalysisResponse.TcoSummary calculateTcoSummary(Long companyId, LocalDate startDate, LocalDate endDate) {
        // Use existing repository method to get summary statistics
        Object[] stats = tcoAnalysisRepository.findTcoSummaryStats(
            companyId,
            startDate.getYear(),
            startDate.getMonthValue()
        );

        if (stats == null || stats[0] == null) {
            return TcoAnalysisResponse.TcoSummary.builder()
                .totalVehicles(0L)
                .averageTotalCost(BigDecimal.ZERO)
                .maxTotalCost(BigDecimal.ZERO)
                .minTotalCost(BigDecimal.ZERO)
                .totalCompanyCost(BigDecimal.ZERO)
                .analysisPeriod(String.format("%s ~ %s", startDate, endDate))
                .build();
        }

        // Extract statistics from the query result
        // [총 차량 수, 평균 TCO, 최고 TCO, 최저 TCO, 총 TCO]
        Long totalVehicles = ((Number) stats[0]).longValue();
        BigDecimal averageCost = stats[1] != null ? new BigDecimal(stats[1].toString()) : BigDecimal.ZERO;
        BigDecimal maxCost = stats[2] != null ? new BigDecimal(stats[2].toString()) : BigDecimal.ZERO;
        BigDecimal minCost = stats[3] != null ? new BigDecimal(stats[3].toString()) : BigDecimal.ZERO;
        BigDecimal totalCost = stats[4] != null ? new BigDecimal(stats[4].toString()) : BigDecimal.ZERO;

        return TcoAnalysisResponse.TcoSummary.builder()
            .totalVehicles(totalVehicles)
            .averageTotalCost(averageCost)
            .maxTotalCost(maxCost)
            .minTotalCost(minCost)
            .totalCompanyCost(totalCost)
            .analysisPeriod(String.format("%s ~ %s", startDate, endDate))
            .build();
    }

    // Helper methods can be added here if needed in the future
}