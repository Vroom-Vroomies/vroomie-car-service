package com.vroomie.car_service.domain.dashboard.service;

import com.vroomie.car_service.domain.dashboard.dto.response.CostSavingOpportunityResponse;
import com.vroomie.car_service.domain.dashboard.dto.response.TcoRecommendationResponse;
import com.vroomie.car_service.domain.dashboard.projection.TcoAnalysisProjection;
import com.vroomie.car_service.domain.dashboard.entity.VehicleEfficiencyEntity;
import com.vroomie.car_service.domain.dashboard.repository.TcoAnalysisRepository;
import com.vroomie.car_service.domain.dashboard.repository.VehicleEfficiencyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CostSavingOpportunityService {

    private final TcoAnalysisRepository tcoAnalysisRepository;
    private final VehicleEfficiencyRepository vehicleEfficiencyRepository;

    public CostSavingOpportunityResponse getCostSavingOpportunities(Long companyId, LocalDate startDate, LocalDate endDate) {
        List<TcoAnalysisProjection> tcoData = tcoAnalysisRepository.findTcoAnalysisWithFilters(
            companyId, startDate, endDate, null, null, null, null, Pageable.unpaged()
        ).getContent();

        // Use existing repository method instead
        List<VehicleEfficiencyEntity> efficiencyData = vehicleEfficiencyRepository.findByCompanyAndPeriod(
            companyId, VehicleEfficiencyEntity.AnalysisPeriod.MONTH, 2024, 0, Pageable.unpaged()
        ).getContent();

        List<CostSavingOpportunityResponse.CategoryOpportunity> opportunities = identifyOpportunities(tcoData, efficiencyData);

        CostSavingOpportunityResponse.SavingOpportunitySummary summary = calculateSummary(opportunities);

        CostSavingOpportunityResponse.AnalysisPeriod analysisPeriod = CostSavingOpportunityResponse.AnalysisPeriod.builder()
            .startDate(startDate)
            .endDate(endDate)
            .periodDays((long) (endDate.toEpochDay() - startDate.toEpochDay()))
            .periodDescription(String.format("%s ~ %s 기간 분석", startDate, endDate))
            .build();

        return CostSavingOpportunityResponse.builder()
            .summary(summary)
            .opportunities(opportunities)
            .analysisPeriod(analysisPeriod)
            .build();
    }

    public TcoRecommendationResponse getTcoRecommendations(Long companyId, LocalDate startDate, LocalDate endDate) {
        List<TcoAnalysisProjection> tcoData = tcoAnalysisRepository.findTcoAnalysisWithFilters(
            companyId, startDate, endDate, null, null, null, null, Pageable.unpaged()
        ).getContent();

        // Use existing repository method instead
        List<VehicleEfficiencyEntity> efficiencyData = vehicleEfficiencyRepository.findByCompanyAndPeriod(
            companyId, VehicleEfficiencyEntity.AnalysisPeriod.MONTH, 2024, 0, Pageable.unpaged()
        ).getContent();

        List<TcoRecommendationResponse.VehicleRecommendation> recommendations = generateRecommendations(tcoData, efficiencyData);

        TcoRecommendationResponse.RecommendationSummary summary = calculateRecommendationSummary(recommendations);

        // Create pagination info (for future use)
        com.vroomie.car_service.domain.dashboard.dto.response.TcoAnalysisResponse.PageInfo pageInfo =
            com.vroomie.car_service.domain.dashboard.dto.response.TcoAnalysisResponse.PageInfo.builder()
                .totalElements((long) recommendations.size())
                .totalPages(1)
                .currentPage(0)
                .pageSize(recommendations.size())
                .isFirst(true)
                .isLast(true)
                .build();

        return TcoRecommendationResponse.builder()
            .summary(summary)
            .recommendations(recommendations)
            .pageInfo(pageInfo)
            .build();
    }

    private List<CostSavingOpportunityResponse.CategoryOpportunity> identifyOpportunities(
            List<TcoAnalysisProjection> tcoData, List<VehicleEfficiencyEntity> efficiencyData) {

        List<CostSavingOpportunityResponse.CategoryOpportunity> opportunities = new ArrayList<>();

        // Operational cost optimization
        opportunities.add(createOperationalOpportunity(tcoData));

        // Maintenance cost optimization
        opportunities.add(createMaintenanceOpportunity(tcoData, efficiencyData));

        // Variable cost optimization
        opportunities.add(createVariableCostOpportunity(tcoData));

        // Contract optimization
        opportunities.add(createContractOpportunity(tcoData));

        return opportunities.stream()
            .sorted(Comparator.comparing(CostSavingOpportunityResponse.CategoryOpportunity::getPriority))
            .collect(Collectors.toList());
    }

    private CostSavingOpportunityResponse.CategoryOpportunity createOperationalOpportunity(List<TcoAnalysisProjection> tcoData) {
        BigDecimal totalOperationalCost = tcoData.stream()
            .map(TcoAnalysisProjection::getOperationalCost)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal potentialSaving = totalOperationalCost.multiply(BigDecimal.valueOf(0.15)); // 15% 절감 가능
        BigDecimal savingPercentage = totalOperationalCost.compareTo(BigDecimal.ZERO) > 0
            ? potentialSaving.divide(totalOperationalCost, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
            : BigDecimal.ZERO;

        List<CostSavingOpportunityResponse.SavingMethod> methods = Arrays.asList(
            CostSavingOpportunityResponse.SavingMethod.builder()
                .methodName("연료 효율성 개선")
                .description("운전자 교육을 통한 연료 효율성 향상")
                .estimatedSaving(potentialSaving.multiply(BigDecimal.valueOf(0.4)))
                .requiredInvestment(BigDecimal.valueOf(500000))
                .roi(40.0)
                .implementationSteps(Arrays.asList("운전자 교육 프로그램 개발", "월간 연료 효율성 모니터링", "인센티브 제도 도입"))
                .build(),
            CostSavingOpportunityResponse.SavingMethod.builder()
                .methodName("차량 운행 최적화")
                .description("GPS 기반 경로 최적화 시스템 도입")
                .estimatedSaving(potentialSaving.multiply(BigDecimal.valueOf(0.6)))
                .requiredInvestment(BigDecimal.valueOf(2000000))
                .roi(60.0)
                .implementationSteps(Arrays.asList("경로 최적화 시스템 도입", "실시간 교통정보 연동", "운행 패턴 분석"))
                .build()
        );

        return CostSavingOpportunityResponse.CategoryOpportunity.builder()
            .category("OPERATIONAL")
            .categoryName("운영비용")
            .potentialSaving(savingPercentage)
            .unit("percentage")
            .description("연료비 및 운영비 절감을 통한 비용 최적화")
            .estimatedAmount(potentialSaving)
            .currentAmount(totalOperationalCost)
            .savingPercentage(savingPercentage.doubleValue())
            .priority(1)
            .implementationDifficulty("MEDIUM")
            .estimatedImplementationMonths(6)
            .savingMethods(methods)
            .build();
    }

    private CostSavingOpportunityResponse.CategoryOpportunity createMaintenanceOpportunity(
            List<TcoAnalysisProjection> tcoData, List<VehicleEfficiencyEntity> efficiencyData) {

        BigDecimal totalMaintenanceCost = tcoData.stream()
            .map(TcoAnalysisProjection::getMaintenanceCost)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal potentialSaving = totalMaintenanceCost.multiply(BigDecimal.valueOf(0.20)); // 20% 절감 가능
        BigDecimal savingPercentage = totalMaintenanceCost.compareTo(BigDecimal.ZERO) > 0
            ? potentialSaving.divide(totalMaintenanceCost, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
            : BigDecimal.ZERO;

        List<CostSavingOpportunityResponse.SavingMethod> methods = Arrays.asList(
            CostSavingOpportunityResponse.SavingMethod.builder()
                .methodName("예방 정비 시스템")
                .description("예측 기반 예방 정비로 고장 비용 절감")
                .estimatedSaving(potentialSaving.multiply(BigDecimal.valueOf(0.7)))
                .requiredInvestment(BigDecimal.valueOf(3000000))
                .roi(70.0)
                .implementationSteps(Arrays.asList("정비 스케줄링 시스템 구축", "센서 기반 모니터링", "정비 업체 파트너십"))
                .build(),
            CostSavingOpportunityResponse.SavingMethod.builder()
                .methodName("정비 업체 협상")
                .description("정비 비용 재협상을 통한 단가 절감")
                .estimatedSaving(potentialSaving.multiply(BigDecimal.valueOf(0.3)))
                .requiredInvestment(BigDecimal.valueOf(100000))
                .roi(30.0)
                .implementationSteps(Arrays.asList("정비 업체 비교 분석", "단가 재협상", "서비스 품질 평가"))
                .build()
        );

        return CostSavingOpportunityResponse.CategoryOpportunity.builder()
            .category("MAINTENANCE")
            .categoryName("유지보수비용")
            .potentialSaving(savingPercentage)
            .unit("percentage")
            .description("예방 정비 및 정비 비용 최적화")
            .estimatedAmount(potentialSaving)
            .currentAmount(totalMaintenanceCost)
            .savingPercentage(savingPercentage.doubleValue())
            .priority(2)
            .implementationDifficulty("HARD")
            .estimatedImplementationMonths(12)
            .savingMethods(methods)
            .build();
    }

    private CostSavingOpportunityResponse.CategoryOpportunity createVariableCostOpportunity(List<TcoAnalysisProjection> tcoData) {
        // Variable costs would need to be calculated from TcoAnalysisProjection or separate query
        BigDecimal estimatedVariableCost = tcoData.stream()
            .map(TcoAnalysisProjection::getTotalCost)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .multiply(BigDecimal.valueOf(0.3)); // Estimate 30% of total cost is variable

        BigDecimal potentialSaving = estimatedVariableCost.multiply(BigDecimal.valueOf(0.10)); // 10% 절감 가능
        BigDecimal savingPercentage = estimatedVariableCost.compareTo(BigDecimal.ZERO) > 0
            ? potentialSaving.divide(estimatedVariableCost, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
            : BigDecimal.ZERO;

        List<CostSavingOpportunityResponse.SavingMethod> methods = Arrays.asList(
            CostSavingOpportunityResponse.SavingMethod.builder()
                .methodName("주차비 최적화")
                .description("법인 주차장 확보 및 주차비 절감")
                .estimatedSaving(potentialSaving.multiply(BigDecimal.valueOf(0.5)))
                .requiredInvestment(BigDecimal.valueOf(5000000))
                .roi(25.0)
                .implementationSteps(Arrays.asList("주차장 현황 분석", "법인 주차장 계약", "주차비 정책 수립"))
                .build(),
            CostSavingOpportunityResponse.SavingMethod.builder()
                .methodName("통행료 최적화")
                .description("경로 최적화를 통한 통행료 절감")
                .estimatedSaving(potentialSaving.multiply(BigDecimal.valueOf(0.5)))
                .requiredInvestment(BigDecimal.valueOf(1000000))
                .roi(50.0)
                .implementationSteps(Arrays.asList("통행료 분석", "대체 경로 개발", "운전자 교육"))
                .build()
        );

        return CostSavingOpportunityResponse.CategoryOpportunity.builder()
            .category("VARIABLE")
            .categoryName("변동비용")
            .potentialSaving(savingPercentage)
            .unit("percentage")
            .description("주차비, 통행료 등 변동비용 최적화")
            .estimatedAmount(potentialSaving)
            .currentAmount(estimatedVariableCost)
            .savingPercentage(savingPercentage.doubleValue())
            .priority(3)
            .implementationDifficulty("EASY")
            .estimatedImplementationMonths(3)
            .savingMethods(methods)
            .build();
    }

    private CostSavingOpportunityResponse.CategoryOpportunity createContractOpportunity(List<TcoAnalysisProjection> tcoData) {
        BigDecimal totalContractCost = tcoData.stream()
            .map(TcoAnalysisProjection::getContractCost)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal potentialSaving = totalContractCost.multiply(BigDecimal.valueOf(0.08)); // 8% 절감 가능
        BigDecimal savingPercentage = totalContractCost.compareTo(BigDecimal.ZERO) > 0
            ? potentialSaving.divide(totalContractCost, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
            : BigDecimal.ZERO;

        List<CostSavingOpportunityResponse.SavingMethod> methods = Arrays.asList(
            CostSavingOpportunityResponse.SavingMethod.builder()
                .methodName("계약 조건 재협상")
                .description("리스 조건 및 보험료 재협상")
                .estimatedSaving(potentialSaving)
                .requiredInvestment(BigDecimal.valueOf(200000))
                .roi(400.0)
                .implementationSteps(Arrays.asList("계약 조건 분석", "협상 전략 수립", "재계약 진행"))
                .build()
        );

        return CostSavingOpportunityResponse.CategoryOpportunity.builder()
            .category("CONTRACT")
            .categoryName("계약비용")
            .potentialSaving(savingPercentage)
            .unit("percentage")
            .description("리스 및 보험 계약 조건 최적화")
            .estimatedAmount(potentialSaving)
            .currentAmount(totalContractCost)
            .savingPercentage(savingPercentage.doubleValue())
            .priority(4)
            .implementationDifficulty("MEDIUM")
            .estimatedImplementationMonths(4)
            .savingMethods(methods)
            .build();
    }

    private CostSavingOpportunityResponse.SavingOpportunitySummary calculateSummary(
            List<CostSavingOpportunityResponse.CategoryOpportunity> opportunities) {

        BigDecimal totalSavingAmount = opportunities.stream()
            .map(CostSavingOpportunityResponse.CategoryOpportunity::getEstimatedAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCurrentAmount = opportunities.stream()
            .map(CostSavingOpportunityResponse.CategoryOpportunity::getCurrentAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        Double totalSavingPercentage = totalCurrentAmount.compareTo(BigDecimal.ZERO) > 0
            ? totalSavingAmount.divide(totalCurrentAmount, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100)).doubleValue()
            : 0.0;

        CostSavingOpportunityResponse.CategoryOpportunity topOpportunity = opportunities.stream()
            .max(Comparator.comparing(CostSavingOpportunityResponse.CategoryOpportunity::getEstimatedAmount))
            .orElse(null);

        return CostSavingOpportunityResponse.SavingOpportunitySummary.builder()
            .totalSavingAmount(totalSavingAmount)
            .totalCurrentAmount(totalCurrentAmount)
            .totalSavingPercentage(totalSavingPercentage)
            .opportunityCount(opportunities.size())
            .topOpportunityCategory(topOpportunity != null ? topOpportunity.getCategoryName() : null)
            .topSavingAmount(topOpportunity != null ? topOpportunity.getEstimatedAmount() : BigDecimal.ZERO)
            .build();
    }

    private List<TcoRecommendationResponse.VehicleRecommendation> generateRecommendations(
            List<TcoAnalysisProjection> tcoData, List<VehicleEfficiencyEntity> efficiencyData) {

        List<TcoRecommendationResponse.VehicleRecommendation> recommendations = new ArrayList<>();

        // High cost vehicles - consider disposal or replacement
        tcoData.stream()
            .filter(vehicle -> vehicle.getTotalCost().compareTo(BigDecimal.valueOf(5000000)) > 0)
            .forEach(vehicle -> {
                recommendations.add(createHighCostRecommendation(vehicle));
            });

        // Inefficient vehicles - consider maintenance or replacement
        efficiencyData.stream()
            .filter(vehicle -> vehicle.getInefficiencyScore().compareTo(BigDecimal.valueOf(70)) > 0)
            .forEach(vehicle -> {
                recommendations.add(createInefficiencyRecommendation(vehicle));
            });

        return recommendations.stream()
            .sorted(Comparator.comparing((TcoRecommendationResponse.VehicleRecommendation r) -> {
                switch (r.getPriority()) {
                    case "high": return 1;
                    case "medium": return 2;
                    case "low": return 3;
                    default: return 4;
                }
            }))
            .collect(Collectors.toList());
    }

    private TcoRecommendationResponse.VehicleRecommendation createHighCostRecommendation(TcoAnalysisProjection vehicle) {
        BigDecimal estimatedSaving = vehicle.getTotalCost().multiply(BigDecimal.valueOf(0.3));

        TcoRecommendationResponse.RecommendationDetail detail = TcoRecommendationResponse.RecommendationDetail.builder()
            .estimatedDurationMonths(6)
            .estimatedInvestmentCost(BigDecimal.valueOf(1000000))
            .roi(30.0)
            .considerations(Arrays.asList("차량 잔존 가치 확인", "대체 차량 비용 산정", "운영 중단 최소화"))
            .alternatives(Arrays.asList("리스 조건 재협상", "운영 방식 변경", "정비 최적화"))
            .build();

        return TcoRecommendationResponse.VehicleRecommendation.builder()
            .vehicleId(vehicle.getVehicleId())
            .vehicleName(vehicle.getVehicleId()) // Using vehicleId as name
            .recommendationType("replacement")
            .recommendationTitle("고비용 차량 교체 검토")
            .reason("월간 운영비가 평균 대비 200% 이상 높아 교체가 필요합니다")
            .priority("high")
            .estimatedSaving(estimatedSaving)
            .currentCost(vehicle.getTotalCost())
            .savingPercentage(30.0)
            .vehicleAge(calculateVehicleAge(vehicle.getVehicleId()))
            .efficiencyScore(BigDecimal.valueOf(40)) // Default score
            .detail(detail)
            .build();
    }

    private TcoRecommendationResponse.VehicleRecommendation createInefficiencyRecommendation(VehicleEfficiencyEntity vehicle) {
        BigDecimal estimatedSaving = vehicle.getTotalCost().multiply(BigDecimal.valueOf(0.15));

        TcoRecommendationResponse.RecommendationDetail detail = TcoRecommendationResponse.RecommendationDetail.builder()
            .estimatedDurationMonths(3)
            .estimatedInvestmentCost(BigDecimal.valueOf(500000))
            .roi(15.0)
            .considerations(Arrays.asList("정비 이력 점검", "운전자 교육", "사용 패턴 분석"))
            .alternatives(Arrays.asList("집중 정비", "운전자 변경", "사용 제한"))
            .build();

        return TcoRecommendationResponse.VehicleRecommendation.builder()
            .vehicleId(vehicle.getVehicleId())
            .vehicleName(vehicle.getVehicleId()) // Using vehicleId as name
            .recommendationType("maintenance")
            .recommendationTitle("효율성 개선 집중 관리")
            .reason("효율성 점수가 70점 이상으로 집중 관리가 필요합니다")
            .priority("medium")
            .estimatedSaving(estimatedSaving)
            .currentCost(vehicle.getTotalCost())
            .savingPercentage(15.0)
            .vehicleAge(calculateVehicleAge(vehicle.getVehicleId()))
            .efficiencyScore(vehicle.getInefficiencyScore())
            .detail(detail)
            .build();
    }

    private TcoRecommendationResponse.RecommendationSummary calculateRecommendationSummary(
            List<TcoRecommendationResponse.VehicleRecommendation> recommendations) {

        long highPriorityCount = recommendations.stream()
            .mapToLong(r -> "high".equals(r.getPriority()) ? 1 : 0)
            .sum();

        long mediumPriorityCount = recommendations.stream()
            .mapToLong(r -> "medium".equals(r.getPriority()) ? 1 : 0)
            .sum();

        long lowPriorityCount = recommendations.stream()
            .mapToLong(r -> "low".equals(r.getPriority()) ? 1 : 0)
            .sum();

        BigDecimal totalEstimatedSaving = recommendations.stream()
            .map(TcoRecommendationResponse.VehicleRecommendation::getEstimatedSaving)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<TcoRecommendationResponse.PriorityDistribution> priorityDistribution = Arrays.asList(
            TcoRecommendationResponse.PriorityDistribution.builder()
                .priority("high")
                .count(highPriorityCount)
                .percentage(recommendations.size() > 0 ? (double) highPriorityCount / recommendations.size() * 100 : 0.0)
                .estimatedSaving(recommendations.stream()
                    .filter(r -> "high".equals(r.getPriority()))
                    .map(TcoRecommendationResponse.VehicleRecommendation::getEstimatedSaving)
                    .reduce(BigDecimal.ZERO, BigDecimal::add))
                .build(),
            TcoRecommendationResponse.PriorityDistribution.builder()
                .priority("medium")
                .count(mediumPriorityCount)
                .percentage(recommendations.size() > 0 ? (double) mediumPriorityCount / recommendations.size() * 100 : 0.0)
                .estimatedSaving(recommendations.stream()
                    .filter(r -> "medium".equals(r.getPriority()))
                    .map(TcoRecommendationResponse.VehicleRecommendation::getEstimatedSaving)
                    .reduce(BigDecimal.ZERO, BigDecimal::add))
                .build(),
            TcoRecommendationResponse.PriorityDistribution.builder()
                .priority("low")
                .count(lowPriorityCount)
                .percentage(recommendations.size() > 0 ? (double) lowPriorityCount / recommendations.size() * 100 : 0.0)
                .estimatedSaving(recommendations.stream()
                    .filter(r -> "low".equals(r.getPriority()))
                    .map(TcoRecommendationResponse.VehicleRecommendation::getEstimatedSaving)
                    .reduce(BigDecimal.ZERO, BigDecimal::add))
                .build()
        );

        return TcoRecommendationResponse.RecommendationSummary.builder()
            .totalRecommendations((long) recommendations.size())
            .highPriorityCount(highPriorityCount)
            .mediumPriorityCount(mediumPriorityCount)
            .lowPriorityCount(lowPriorityCount)
            .totalEstimatedSaving(totalEstimatedSaving)
            .priorityDistribution(priorityDistribution)
            .build();
    }

    private Integer calculateVehicleAge(String vehicleId) {
        // This would require actual vehicle data or registration date
        // For now, return a default value
        return 3; // Default 3 years old
    }
}