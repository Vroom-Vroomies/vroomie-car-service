package com.vroomie.car_service.domain.dashboard.service;

import com.vroomie.car_service.domain.dashboard.dto.request.VehicleEfficiencyRequest;
import com.vroomie.car_service.domain.dashboard.dto.response.VehicleEfficiencyResponse;
import com.vroomie.car_service.domain.dashboard.dto.response.EfficiencyTrendResponse;
import com.vroomie.car_service.domain.dashboard.entity.VehicleEfficiencyEntity;
import com.vroomie.car_service.domain.dashboard.repository.VehicleEfficiencyRepository;
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
public class VehicleEfficiencyService {

    private final VehicleEfficiencyRepository vehicleEfficiencyRepository;

    public VehicleEfficiencyResponse getVehicleEfficiencyAnalysis(VehicleEfficiencyRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());

        // Use existing repository method instead of the new one I added
        Page<VehicleEfficiencyEntity> efficiencyPage = vehicleEfficiencyRepository.findByCompanyAndPeriod(
            request.getCompanyId(),
            request.getPeriod() != null ? request.getPeriod() : VehicleEfficiencyEntity.AnalysisPeriod.MONTH,
            request.getYear() != null ? request.getYear() : 2024,
            request.getMonth() != null ? request.getMonth() : 0,
            pageable
        );

        List<VehicleEfficiencyResponse.VehicleEfficiencyData> vehicleData = efficiencyPage.getContent().stream()
            .map(this::mapToVehicleEfficiencyData)
            .collect(Collectors.toList());

        VehicleEfficiencyResponse.EfficiencySummary summary = calculateEfficiencySummary(
            request.getCompanyId(),
            request.getPeriod(),
            request.getYear(),
            request.getMonth()
        );

        List<VehicleEfficiencyResponse.EfficiencyDistribution> distribution = getEfficiencyDistribution(
            request.getCompanyId(),
            request.getPeriod(),
            request.getYear(),
            request.getMonth()
        );

        // Reuse the same PageInfo structure from TcoAnalysisResponse
        com.vroomie.car_service.domain.dashboard.dto.response.TcoAnalysisResponse.PageInfo pageInfo =
            com.vroomie.car_service.domain.dashboard.dto.response.TcoAnalysisResponse.PageInfo.builder()
                .totalElements(efficiencyPage.getTotalElements())
                .totalPages(efficiencyPage.getTotalPages())
                .currentPage(efficiencyPage.getNumber())
                .pageSize(efficiencyPage.getSize())
                .isFirst(efficiencyPage.isFirst())
                .isLast(efficiencyPage.isLast())
                .build();

        return VehicleEfficiencyResponse.builder()
            .summary(summary)
            .vehicles(vehicleData)
            .distribution(distribution)
            .pageInfo(pageInfo)
            .build();
    }

    public EfficiencyTrendResponse getEfficiencyTrend(Long companyId, LocalDate startDate, LocalDate endDate) {
        // Use existing repository method for trend analysis
        List<VehicleEfficiencyEntity> currentTrends = vehicleEfficiencyRepository.findByCompanyAndPeriod(
            companyId, VehicleEfficiencyEntity.AnalysisPeriod.MONTH, startDate.getYear(), startDate.getMonthValue(), Pageable.unpaged()
        ).getContent();

        List<VehicleEfficiencyEntity> previousTrends = vehicleEfficiencyRepository.findByCompanyAndPeriod(
            companyId, VehicleEfficiencyEntity.AnalysisPeriod.MONTH, startDate.minusMonths(1).getYear(), startDate.minusMonths(1).getMonthValue(), Pageable.unpaged()
        ).getContent();

        EfficiencyTrendResponse.TrendSummary summary = calculateTrendSummary(currentTrends, previousTrends);

        List<EfficiencyTrendResponse.VehicleTrendData> vehicleTrends = currentTrends.stream()
            .map(current -> {
                VehicleEfficiencyEntity previous = findPreviousTrend(previousTrends, current.getVehicleId());
                return mapToVehicleTrendData(current, previous);
            })
            .collect(Collectors.toList());

        List<EfficiencyTrendResponse.MonthlyTrendData> monthlyTrends = getMonthlyTrendData(
            companyId, startDate, endDate
        );

        EfficiencyTrendResponse.TrendAnalysisPeriod analysisPeriod = EfficiencyTrendResponse.TrendAnalysisPeriod.builder()
            .currentPeriodStart(startDate)
            .currentPeriodEnd(endDate)
            .previousPeriodStart(startDate.minusMonths(1))
            .previousPeriodEnd(endDate.minusMonths(1))
            .periodDescription("월간 효율성 트렌드 분석")
            .comparisonType("MONTH_TO_MONTH")
            .build();

        return EfficiencyTrendResponse.builder()
            .summary(summary)
            .vehicleTrends(vehicleTrends)
            .monthlyTrends(monthlyTrends)
            .analysisPeriod(analysisPeriod)
            .build();
    }

    private VehicleEfficiencyResponse.VehicleEfficiencyData mapToVehicleEfficiencyData(VehicleEfficiencyEntity entity) {
        VehicleEfficiencyResponse.EfficiencyTrend monthlyTrend = VehicleEfficiencyResponse.EfficiencyTrend.builder()
            .inefficiencyScoreChange(BigDecimal.ZERO) // Will be calculated with previous month data
            .costPerKmChange(BigDecimal.ZERO)
            .fuelEfficiencyChange(BigDecimal.ZERO)
            .isImproved(false)
            .changePercentage(0.0)
            .trendDirection("STABLE")
            .build();

        return VehicleEfficiencyResponse.VehicleEfficiencyData.builder()
            .vehicleId(entity.getVehicleId())
            .vehicleName(entity.getVehicleId()) // Using vehicleId as name since vehicleName doesn't exist
            .inefficiencyScore(entity.getInefficiencyScore())
            .efficiencyGrade(calculateEfficiencyGrade(entity.getInefficiencyScore()))
            .totalCost(entity.getTotalCost())
            .totalDistance(entity.getTotalDistance())
            .costPerKm(entity.getCostPerKm())
            .usageDays(entity.getUsageDays())
            .fuelEfficiency(entity.getFuelEfficiency())
            .maintenanceFrequency(entity.getMaintenanceFrequency())
            .improvementPotential(calculateImprovementPotential(entity.getInefficiencyScore()))
            .rankAmongSimilar(null) // Will be calculated during ranking
            .monthlyTrend(monthlyTrend)
            .build();
    }

    private VehicleEfficiencyResponse.EfficiencySummary calculateEfficiencySummary(
            Long companyId, VehicleEfficiencyEntity.AnalysisPeriod period, Integer year, Integer month) {

        // Use existing repository method to get all vehicles
        List<VehicleEfficiencyEntity> allVehicles = vehicleEfficiencyRepository.findByCompanyAndPeriod(
            companyId, period, year, month, Pageable.unpaged()
        ).getContent();

        if (allVehicles.isEmpty()) {
            return VehicleEfficiencyResponse.EfficiencySummary.builder()
                .totalVehicles(0L)
                .averageInefficiencyScore(BigDecimal.ZERO)
                .averageCostPerKm(BigDecimal.ZERO)
                .averageFuelEfficiency(BigDecimal.ZERO)
                .vehiclesNeedingImprovement(0L)
                .improvementNeededPercentage(0.0)
                .period(period)
                .year(year)
                .month(month)
                .build();
        }

        BigDecimal totalInefficiencyScore = allVehicles.stream()
            .map(VehicleEfficiencyEntity::getInefficiencyScore)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCostPerKm = allVehicles.stream()
            .map(VehicleEfficiencyEntity::getCostPerKm)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalFuelEfficiency = allVehicles.stream()
            .map(VehicleEfficiencyEntity::getFuelEfficiency)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        long vehiclesNeedingImprovement = allVehicles.stream()
            .mapToLong(v -> v.getInefficiencyScore().compareTo(BigDecimal.valueOf(60)) > 0 ? 1 : 0)
            .sum();

        return VehicleEfficiencyResponse.EfficiencySummary.builder()
            .totalVehicles((long) allVehicles.size())
            .averageInefficiencyScore(totalInefficiencyScore.divide(
                BigDecimal.valueOf(allVehicles.size()), 2, RoundingMode.HALF_UP))
            .averageCostPerKm(totalCostPerKm.divide(
                BigDecimal.valueOf(allVehicles.size()), 2, RoundingMode.HALF_UP))
            .averageFuelEfficiency(totalFuelEfficiency.divide(
                BigDecimal.valueOf(allVehicles.size()), 2, RoundingMode.HALF_UP))
            .vehiclesNeedingImprovement(vehiclesNeedingImprovement)
            .improvementNeededPercentage((double) vehiclesNeedingImprovement / allVehicles.size() * 100)
            .period(period)
            .year(year)
            .month(month)
            .build();
    }

    private List<VehicleEfficiencyResponse.EfficiencyDistribution> getEfficiencyDistribution(
            Long companyId, VehicleEfficiencyEntity.AnalysisPeriod period, Integer year, Integer month) {

        // Use existing repository method that returns Object[]
        List<Object[]> distributionData = vehicleEfficiencyRepository.findEfficiencyDistribution(
            companyId, year != null ? year : 2024, month != null ? month : 0
        );

        return distributionData.stream()
            .map(data -> {
                String efficiencyGrade = (String) data[0];
                Long vehicleCount = (Long) data[1];
                BigDecimal avgInefficiencyScore = (BigDecimal) data[2];
                BigDecimal avgCostPerKm = (BigDecimal) data[3];
                Double percentage = vehicleCount.doubleValue(); // Will be calculated properly in a real implementation

                return VehicleEfficiencyResponse.EfficiencyDistribution.builder()
                    .efficiencyGrade(efficiencyGrade)
                    .gradeName(getGradeName(efficiencyGrade))
                    .vehicleCount(vehicleCount)
                    .percentage(percentage)
                    .avgInefficiencyScore(avgInefficiencyScore)
                    .avgCostPerKm(avgCostPerKm)
                    .scoreRange(VehicleEfficiencyResponse.ScoreRange.builder()
                        .minScore(getScoreRangeMin(efficiencyGrade))
                        .maxScore(getScoreRangeMax(efficiencyGrade))
                        .description(getScoreRangeDescription(efficiencyGrade))
                        .build())
                    .build();
            })
            .collect(Collectors.toList());
    }

    private EfficiencyTrendResponse.TrendSummary calculateTrendSummary(
            List<VehicleEfficiencyEntity> currentTrends, List<VehicleEfficiencyEntity> previousTrends) {

        long totalVehicles = currentTrends.size();
        long improvedVehicles = 0;
        long deterioratedVehicles = 0;
        long stableVehicles = 0;

        BigDecimal totalEfficiencyChange = BigDecimal.ZERO;
        String mostImprovedVehicleId = null;
        BigDecimal maxImprovementScore = BigDecimal.valueOf(-1000);

        for (VehicleEfficiencyEntity current : currentTrends) {
            VehicleEfficiencyEntity previous = findPreviousTrend(previousTrends, current.getVehicleId());

            if (previous != null) {
                BigDecimal change = current.getInefficiencyScore().subtract(previous.getInefficiencyScore());
                totalEfficiencyChange = totalEfficiencyChange.add(change);

                if (change.compareTo(BigDecimal.valueOf(-5)) < 0) { // Improved (lower inefficiency)
                    improvedVehicles++;
                    if (change.compareTo(maxImprovementScore) < 0) {
                        maxImprovementScore = change;
                        mostImprovedVehicleId = current.getVehicleId();
                    }
                } else if (change.compareTo(BigDecimal.valueOf(5)) > 0) { // Deteriorated
                    deterioratedVehicles++;
                } else { // Stable
                    stableVehicles++;
                }
            } else {
                stableVehicles++;
            }
        }

        BigDecimal averageEfficiencyChange = totalVehicles > 0
            ? totalEfficiencyChange.divide(BigDecimal.valueOf(totalVehicles), 2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;

        double overallImprovementRate = totalVehicles > 0
            ? (double) improvedVehicles / totalVehicles * 100
            : 0.0;

        return EfficiencyTrendResponse.TrendSummary.builder()
            .totalVehicles(totalVehicles)
            .improvedVehicles(improvedVehicles)
            .deterioratedVehicles(deterioratedVehicles)
            .stableVehicles(stableVehicles)
            .overallImprovementRate(overallImprovementRate)
            .averageEfficiencyChange(averageEfficiencyChange)
            .mostImprovedVehicleId(mostImprovedVehicleId)
            .maxImprovementScore(maxImprovementScore.abs())
            .build();
    }

    private EfficiencyTrendResponse.VehicleTrendData mapToVehicleTrendData(
            VehicleEfficiencyEntity current, VehicleEfficiencyEntity previous) {

        BigDecimal efficiencyScoreChange = previous != null
            ? current.getInefficiencyScore().subtract(previous.getInefficiencyScore())
            : BigDecimal.ZERO;

        double changePercentage = previous != null && previous.getInefficiencyScore().compareTo(BigDecimal.ZERO) > 0
            ? efficiencyScoreChange.divide(previous.getInefficiencyScore(), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100)).doubleValue()
            : 0.0;

        String trendDirection = determineTrendDirection(efficiencyScoreChange);

        return EfficiencyTrendResponse.VehicleTrendData.builder()
            .vehicleId(current.getVehicleId())
            .vehicleName(current.getVehicleId()) // Using vehicleId as name
            .currentEfficiencyScore(current.getInefficiencyScore())
            .previousEfficiencyScore(previous != null ? previous.getInefficiencyScore() : BigDecimal.ZERO)
            .efficiencyScoreChange(efficiencyScoreChange)
            .changePercentage(changePercentage)
            .trendDirection(trendDirection)
            .improvementRank(null) // Will be calculated during ranking
            .consecutiveImprovementMonths(0) // Requires historical data analysis
            .predictedNextScore(predictNextScore(current.getInefficiencyScore(), efficiencyScoreChange))
            .build();
    }

    private List<EfficiencyTrendResponse.MonthlyTrendData> getMonthlyTrendData(
            Long companyId, LocalDate startDate, LocalDate endDate) {
        // This would require monthly aggregation logic
        // For now, return empty list or implement based on available data
        return List.of();
    }

    private VehicleEfficiencyEntity findPreviousTrend(List<VehicleEfficiencyEntity> previousTrends, String vehicleId) {
        return previousTrends.stream()
            .filter(trend -> trend.getVehicleId().equals(vehicleId))
            .findFirst()
            .orElse(null);
    }

    private String calculateEfficiencyGrade(BigDecimal inefficiencyScore) {
        if (inefficiencyScore.compareTo(BigDecimal.valueOf(20)) <= 0) return "A";
        if (inefficiencyScore.compareTo(BigDecimal.valueOf(40)) <= 0) return "B";
        if (inefficiencyScore.compareTo(BigDecimal.valueOf(60)) <= 0) return "C";
        if (inefficiencyScore.compareTo(BigDecimal.valueOf(80)) <= 0) return "D";
        return "F";
    }

    private Double calculateImprovementPotential(BigDecimal inefficiencyScore) {
        // Higher inefficiency score means more room for improvement
        return inefficiencyScore.doubleValue();
    }

    private String getGradeName(String grade) {
        switch (grade) {
            case "VERY_EFFICIENT": return "매우 효율적";
            case "EFFICIENT": return "효율적";
            case "MODERATE": return "보통";
            case "INEFFICIENT": return "비효율적";
            case "VERY_INEFFICIENT": return "매우 비효율적";
            default: return grade;
        }
    }

    private BigDecimal getScoreRangeMin(String grade) {
        switch (grade) {
            case "VERY_EFFICIENT": return BigDecimal.ZERO;
            case "EFFICIENT": return BigDecimal.valueOf(20);
            case "MODERATE": return BigDecimal.valueOf(40);
            case "INEFFICIENT": return BigDecimal.valueOf(60);
            case "VERY_INEFFICIENT": return BigDecimal.valueOf(80);
            default: return BigDecimal.ZERO;
        }
    }

    private BigDecimal getScoreRangeMax(String grade) {
        switch (grade) {
            case "VERY_EFFICIENT": return BigDecimal.valueOf(20);
            case "EFFICIENT": return BigDecimal.valueOf(40);
            case "MODERATE": return BigDecimal.valueOf(60);
            case "INEFFICIENT": return BigDecimal.valueOf(80);
            case "VERY_INEFFICIENT": return BigDecimal.valueOf(100);
            default: return BigDecimal.valueOf(100);
        }
    }

    private String getScoreRangeDescription(String grade) {
        switch (grade) {
            case "VERY_EFFICIENT": return "0-20점: 매우 효율적";
            case "EFFICIENT": return "21-40점: 효율적";
            case "MODERATE": return "41-60점: 보통";
            case "INEFFICIENT": return "61-80점: 비효율적";
            case "VERY_INEFFICIENT": return "81-100점: 매우 비효율적";
            default: return grade;
        }
    }

    private String determineTrendDirection(BigDecimal change) {
        if (change.compareTo(BigDecimal.valueOf(-5)) < 0) return "IMPROVING";
        if (change.compareTo(BigDecimal.valueOf(5)) > 0) return "DECLINING";
        return "STABLE";
    }

    private BigDecimal predictNextScore(BigDecimal currentScore, BigDecimal change) {
        // Simple linear prediction based on current trend
        BigDecimal predicted = currentScore.add(change);
        // Ensure it stays within 0-100 range
        if (predicted.compareTo(BigDecimal.ZERO) < 0) return BigDecimal.ZERO;
        if (predicted.compareTo(BigDecimal.valueOf(100)) > 0) return BigDecimal.valueOf(100);
        return predicted;
    }
}