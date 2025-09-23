package com.vroomie.car_service.domain.dashboard.service;

import com.vroomie.car_service.domain.dashboard.config.FeeTypeConfig;
import com.vroomie.car_service.domain.dashboard.dto.response.MaintenanceBreakdownResponse;
import com.vroomie.car_service.domain.dashboard.dto.response.MaintenanceBreakdownResponse.MaintenanceBreakdownItem;
import com.vroomie.car_service.domain.dashboard.dto.response.MaintenanceBreakdownResponse.MaintenanceTrends;
import com.vroomie.car_service.domain.dashboard.dto.response.MaintenanceBreakdownResponse.MonthlyTrend;
import com.vroomie.car_service.domain.dashboard.dto.response.MonthlyOperatingCostResponse;
import com.vroomie.car_service.domain.dashboard.dto.response.MonthlyOperatingCostResponse.CostSummary;
import com.vroomie.car_service.domain.dashboard.dto.response.MonthlyOperatingCostResponse.DateRange;
import com.vroomie.car_service.domain.dashboard.dto.response.MonthlyOperatingCostResponse.MonthlyData;
import com.vroomie.car_service.domain.dashboard.dto.response.MonthlyOperatingCostResponse.VariableCostItem;
import com.vroomie.car_service.domain.dashboard.dto.response.VehicleMaintenanceResponse;
import com.vroomie.car_service.domain.dashboard.dto.response.VehicleMaintenanceResponse.MaintenanceSummary;
import com.vroomie.car_service.domain.dashboard.dto.response.VehicleMaintenanceResponse.MonthlyComparison;
import com.vroomie.car_service.domain.dashboard.dto.response.VehicleMaintenanceResponse.VehicleMaintenanceData;
import com.vroomie.car_service.domain.dashboard.projection.MaintenanceBreakdownProjection;
import com.vroomie.car_service.domain.dashboard.projection.MonthlyCostProjection;
import com.vroomie.car_service.domain.dashboard.projection.VehicleMaintenanceProjection;
import com.vroomie.car_service.domain.dashboard.repository.DashboardContractRepository;
import com.vroomie.car_service.domain.dashboard.repository.DashboardCostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 대시보드 비용 분석 서비스
 * 월별 운영 비용, 차량 유지보수 비용, 유지보수 분석 등의 비즈니스 로직을 제공합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardCostService {

    private final DashboardCostRepository costRepository;
    private final DashboardContractRepository contractRepository;
    private final FeeTypeConfig feeTypeConfig;

    /**
     * 월별 운영 비용을 조회합니다.
     * 고정비용과 변동비용을 종합하여 월별 운영 비용 분석 데이터를 제공합니다.
     *
     * @param companyId 회사 ID
     * @param period 조회 기간 (month, quarter, year)
     * @param startDate 조회 시작일
     * @param endDate 조회 종료일
     * @return 월별 운영 비용 응답 데이터
     */
    public MonthlyOperatingCostResponse getMonthlyCosts(Long companyId, String period, LocalDate startDate, LocalDate endDate) {
        log.info("[DashboardCostService] 월별 운영 비용 조회 시작 - companyId: {}, period: {}", companyId, period);

        // 날짜 범위 조정
        DateRange dateRange = adjustDateRange(period, startDate, endDate);
        LocalDate adjustedStartDate = LocalDate.parse(dateRange.getStartDate());
        LocalDate adjustedEndDate = LocalDate.parse(dateRange.getEndDate());

        // 변동 비용 조회 (LocalDate를 LocalDateTime으로 변환)
        LocalDateTime startDateTime = adjustedStartDate.atStartOfDay();
        LocalDateTime endDateTime = adjustedEndDate.atTime(23, 59, 59);
        List<MonthlyCostProjection> variableCosts = costRepository.findVariableCostsByMonth(
            companyId, startDateTime, endDateTime
        );

        // 고정 비용 조회 (계약료)
        List<MonthlyCostProjection> fixedCosts = contractRepository.findFixedContractCosts(companyId);

        // 월별 데이터 그룹핑
        Map<String, List<MonthlyCostProjection>> groupedByMonth = groupCostsByMonth(variableCosts);

        // 월별 데이터 구성
        List<MonthlyData> monthlyData = buildMonthlyData(groupedByMonth, fixedCosts);

        // 요약 정보 계산
        CostSummary summary = calculateSummary(monthlyData);

        log.info("[DashboardCostService] 월별 운영 비용 조회 완료 - 총 {}개월 데이터", monthlyData.size());

        return MonthlyOperatingCostResponse.builder()
            .period(period)
            .dateRange(dateRange)
            .monthlyData(monthlyData)
            .summary(summary)
            .build();
    }

    /**
     * 차량별 유지보수 비용을 조회합니다.
     * 특정 차량들의 유지보수 관련 비용을 분석하여 제공합니다.
     *
     * @param companyId 회사 ID
     * @param period 조회 기간
     * @param vehicleIds 조회할 차량 ID 리스트
     * @param startDate 조회 시작일
     * @param endDate 조회 종료일
     * @param limit 조회할 최대 차량 수
     * @return 차량별 유지보수 비용 응답 데이터
     */
    public VehicleMaintenanceResponse getVehicleMaintenanceCosts(
            Long companyId, String period, List<String> vehicleIds,
            LocalDate startDate, LocalDate endDate, int limit) {

        log.info("[DashboardCostService] 차량별 유지보수 비용 조회 시작 - companyId: {}, limit: {}", companyId, limit);

        Pageable pageable = PageRequest.of(0, limit);

        // 차량별 유지보수 비용 조회 (LocalDate를 LocalDateTime으로 변환)
        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : LocalDate.now().minusMonths(1).atStartOfDay();
        LocalDateTime endDateTime = endDate != null ? endDate.atTime(23, 59, 59) : LocalDate.now().atTime(23, 59, 59);
        List<VehicleMaintenanceProjection> projections = costRepository.findVehicleVariableCosts(
            companyId, startDateTime, endDateTime, vehicleIds, pageable
        );

        // 차량별 그룹핑
        Map<String, List<VehicleMaintenanceProjection>> groupedByVehicle = projections.stream()
            .collect(Collectors.groupingBy(VehicleMaintenanceProjection::getVehicleId));

        // 차량별 데이터 구성
        List<VehicleMaintenanceData> vehicles = new ArrayList<>();
        for (Map.Entry<String, List<VehicleMaintenanceProjection>> entry : groupedByVehicle.entrySet()) {
            VehicleMaintenanceData vehicleData = buildVehicleData(entry.getKey(), entry.getValue());
            vehicles.add(vehicleData);
        }

        // 월별 비교 분석
        MonthlyComparison comparison = buildMonthlyComparison(companyId, startDateTime, endDateTime);

        // 요약 정보 구성
        MaintenanceSummary summary = buildMaintenanceSummary(vehicles);

        log.info("[DashboardCostService] 차량별 유지보수 비용 조회 완료 - {}대 차량 분석", vehicles.size());

        return VehicleMaintenanceResponse.builder()
            .vehicles(vehicles)
            .monthlyComparison(comparison)
            .summary(summary)
            .build();
    }

    /**
     * 유지보수 비용 분석을 조회합니다.
     * 유지보수 관련 비용을 유형별로 분석하여 제공합니다.
     *
     * @param companyId 회사 ID
     * @param period 조회 기간
     * @param month 조회 월 (YYYY-MM 형식)
     * @return 유지보수 비용 분석 응답 데이터
     */
    public MaintenanceBreakdownResponse getMaintenanceBreakdown(Long companyId, String period, String month) {
        log.info("[DashboardCostService] 유지보수 비용 분석 조회 시작 - companyId: {}, month: {}", companyId, month);

        LocalDate[] dateRange = parseMonthToDateRange(month);

        // 유지보수 비용 분석 조회 (상세 데이터 포함)
        LocalDateTime startDateTime = dateRange[0].atStartOfDay();
        LocalDateTime endDateTime = dateRange[1].atTime(23, 59, 59);
        List<MaintenanceBreakdownProjection> projections = costRepository.findMaintenanceBreakdownWithDetails(
            companyId, startDateTime, endDateTime
        );

        // 전월 데이터 조회 (증감률 계산용)
        LocalDate previousMonth = dateRange[0].minusMonths(1);
        LocalDate[] previousDateRange = parseMonthToDateRange(previousMonth.toString().substring(0, 7));
        LocalDateTime prevStartDateTime = previousDateRange[0].atStartOfDay();
        LocalDateTime prevEndDateTime = previousDateRange[1].atTime(23, 59, 59);
        List<MaintenanceBreakdownProjection> previousProjections = costRepository.findMaintenanceBreakdown(
            companyId, prevStartDateTime, prevEndDateTime
        );

        // 전월 데이터를 Map으로 변환 (빠른 조회용)
        Map<String, BigDecimal> previousMonthAmounts = new HashMap<>();
        for (MaintenanceBreakdownProjection prev : previousProjections) {
            previousMonthAmounts.put(prev.getFeeType(), prev.getAmount());
        }

        // 총 비용 계산
        BigDecimal total = BigDecimal.ZERO;
        for (MaintenanceBreakdownProjection projection : projections) {
            total = total.add(projection.getAmount());
        }

        // 분석 항목 구성
        List<MaintenanceBreakdownItem> breakdown = new ArrayList<>();
        for (MaintenanceBreakdownProjection p : projections) {
            // 전월 대비 증감률 계산
            BigDecimal currentAmount = p.getAmount() != null ? p.getAmount() : BigDecimal.ZERO;
            BigDecimal previousAmount = previousMonthAmounts.getOrDefault(p.getFeeType(), BigDecimal.ZERO);
            BigDecimal changeRate = BigDecimal.ZERO;
            String changeStatus = "STABLE";

            if (previousAmount.compareTo(BigDecimal.ZERO) > 0) {
                changeRate = currentAmount.subtract(previousAmount)
                    .divide(previousAmount, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));

                if (changeRate.compareTo(BigDecimal.valueOf(5)) > 0) {
                    changeStatus = "INCREASE";
                } else if (changeRate.compareTo(BigDecimal.valueOf(-5)) < 0) {
                    changeStatus = "DECREASE";
                }
            } else if (currentAmount.compareTo(BigDecimal.ZERO) > 0) {
                changeStatus = "INCREASE";
                changeRate = BigDecimal.valueOf(100); // 전월 0원에서 증가한 경우
            }

            // 발생 건수 및 평균 건당 비용 계산
            Long incidentCount = p.getIncidentCount() != null ? p.getIncidentCount() : 1L;
            BigDecimal averagePerIncident = incidentCount > 0 ?
                currentAmount.divide(BigDecimal.valueOf(incidentCount), 2, RoundingMode.HALF_UP) :
                BigDecimal.ZERO;

            MaintenanceBreakdownItem item = MaintenanceBreakdownItem.builder()
                .feeTypeId(p.getFeeType())
                .feeTypeName(getFeeTypeDisplayName(p.getFeeType()))
                .amount(currentAmount)
                .percentage(total.compareTo(BigDecimal.ZERO) > 0 ?
                    currentAmount.divide(total, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)) :
                    BigDecimal.ZERO)
                .color(getFeeTypeColor(p.getFeeType()))
                .changeRate(changeRate)
                .changeStatus(changeStatus)
                .averagePerIncident(averagePerIncident)
                .incidentCount(incidentCount.intValue())
                .build();
            breakdown.add(item);
        }

        // 추세 정보 계산
        MaintenanceTrends trends = calculateTrends(companyId, startDateTime, endDateTime);

        log.info("[DashboardCostService] 유지보수 비용 분석 조회 완료 - 총 비용: {}", total);

        return MaintenanceBreakdownResponse.builder()
            .total(total)
            .period(period)
            .date(month)
            .breakdown(breakdown)
            .trends(trends)
            .build();
    }

    /**
     * 기간에 따라 날짜 범위를 조정합니다.
     */
    private DateRange adjustDateRange(String period, LocalDate startDate, LocalDate endDate) {
        LocalDate adjustedStart = startDate != null ? startDate : LocalDate.now().minusMonths(6);
        LocalDate adjustedEnd = endDate != null ? endDate : LocalDate.now();

        switch (period.toLowerCase()) {
            case "quarter":
                adjustedStart = adjustedEnd.minusMonths(3);
                break;
            case "year":
                adjustedStart = adjustedEnd.minusYears(1);
                break;
            default: // month
                adjustedStart = adjustedEnd.minusMonths(6);
                break;
        }

        return DateRange.builder()
            .startDate(adjustedStart.toString())
            .endDate(adjustedEnd.toString())
            .build();
    }

    /**
     * 비용 데이터를 월별로 그룹핑합니다.
     */
    private Map<String, List<MonthlyCostProjection>> groupCostsByMonth(List<MonthlyCostProjection> costs) {
        Map<String, List<MonthlyCostProjection>> grouped = new HashMap<>();
        for (MonthlyCostProjection cost : costs) {
            grouped.computeIfAbsent(cost.getMonth(), k -> new ArrayList<>()).add(cost);
        }
        return grouped;
    }

    /**
     * 월별 데이터를 구성합니다.
     */
    private List<MonthlyData> buildMonthlyData(Map<String, List<MonthlyCostProjection>> groupedByMonth,
                                               List<MonthlyCostProjection> fixedCosts) {
        List<MonthlyData> monthlyData = new ArrayList<>();

        for (Map.Entry<String, List<MonthlyCostProjection>> entry : groupedByMonth.entrySet()) {
            String month = entry.getKey();
            List<MonthlyCostProjection> monthlyCosts = entry.getValue();

            // 고정 비용 계산
            BigDecimal fixedCost = fixedCosts.stream()
                .map(MonthlyCostProjection::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            // 변동 비용 항목 구성
            List<VariableCostItem> variableCostItems = new ArrayList<>();
            BigDecimal variableTotal = BigDecimal.ZERO;

            for (MonthlyCostProjection cost : monthlyCosts) {
                VariableCostItem item = VariableCostItem.builder()
                    .feeTypeId(cost.getFeeType())
                    .amount(cost.getAmount())
                    .color(getFeeTypeColor(cost.getFeeType()))
                    .build();
                variableCostItems.add(item);
                variableTotal = variableTotal.add(cost.getAmount());
            }

            BigDecimal total = fixedCost.add(variableTotal);

            MonthlyData data = MonthlyData.builder()
                .month(month)
                .fixedCost(fixedCost)
                .variableCost(variableCostItems)
                .total(total)
                .previousMonthDiff(BigDecimal.ZERO) // 이전 월 비교 (향후 구현 예정)
                .build();

            monthlyData.add(data);
        }

        return monthlyData;
    }

    /**
     * 차량별 데이터를 구성합니다.
     */
    private VehicleMaintenanceData buildVehicleData(String vehicleId, List<VehicleMaintenanceProjection> costs) {
        Map<String, BigDecimal> costMap = new HashMap<>();
        BigDecimal total = BigDecimal.ZERO;
        String vehicleName = costs.isEmpty() ? vehicleId : costs.get(0).getVehicleName();

        for (VehicleMaintenanceProjection cost : costs) {
            costMap.merge(cost.getFeeType(), cost.getAmount(), BigDecimal::add);
            total = total.add(cost.getAmount());
        }

        String period = costs.isEmpty() ? "" : costs.get(0).getPeriod();

        return VehicleMaintenanceData.builder()
            .vehicleId(vehicleId)
            .vehicleName(vehicleName)
            .costs(costMap)
            .total(total)
            .period(period)
            .costLevel(determineCostLevel(total))
            .majorCostType(findMajorCostType(costMap))
            .changeRate(BigDecimal.ZERO) // 전월 대비 계산 (향후 구현 예정)
            .build();
    }

    /**
     * 비용 요약 정보를 계산합니다.
     */
    private CostSummary calculateSummary(List<MonthlyData> monthlyData) {
        BigDecimal totalCost = monthlyData.stream()
            .map(MonthlyData::getTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal averageMonthly = monthlyData.isEmpty() ?
            BigDecimal.ZERO :
            totalCost.divide(BigDecimal.valueOf(monthlyData.size()), 2, RoundingMode.HALF_UP);

        return CostSummary.builder()
            .totalCost(totalCost)
            .averageMonthly(averageMonthly)
            .trend("STABLE") // 추세 계산 (향후 구현 예정)
            .trendPercentage(BigDecimal.ZERO)
            .peakMonth(findPeakMonth(monthlyData))
            .lowestMonth(findLowestMonth(monthlyData))
            .build();
    }

    /**
     * 월별 비교 분석을 구성합니다.
     */
    private MonthlyComparison buildMonthlyComparison(Long companyId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        // 실제 월별 비교 로직 (향후 구현 예정)
        return MonthlyComparison.builder()
            .thisMonth(new HashMap<>())
            .lastMonth(new HashMap<>())
            .monthlyChangeRate(BigDecimal.ZERO)
            .mostIncreasedType("maintenance")
            .mostDecreasedType("repair")
            .build();
    }

    /**
     * 유지보수 요약 정보를 구성합니다.
     */
    private MaintenanceSummary buildMaintenanceSummary(List<VehicleMaintenanceData> vehicles) {
        BigDecimal totalCost = vehicles.stream()
            .map(VehicleMaintenanceData::getTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal averageCost = vehicles.isEmpty() ?
            BigDecimal.ZERO :
            totalCost.divide(BigDecimal.valueOf(vehicles.size()), 2, RoundingMode.HALF_UP);

        return MaintenanceSummary.builder()
            .totalVehicles(vehicles.size())
            .totalMaintenanceCost(totalCost)
            .averageCostPerVehicle(averageCost)
            .highestCostVehicle(findHighestCostVehicle(vehicles))
            .lowestCostVehicle(findLowestCostVehicle(vehicles))
            .primaryCostType("maintenance")
            .primaryCostRatio(BigDecimal.valueOf(60))
            .build();
    }

    /**
     * 추세 정보를 계산합니다.
     * 지난 3개월간의 데이터를 분석하여 추세를 제공합니다.
     */
    private MaintenanceTrends calculateTrends(Long companyId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        // 지난 3개월간의 데이터 조회 (현재 월 포함)
        LocalDateTime trendStartDateTime = startDateTime.minusMonths(3);
        List<MonthlyCostProjection> trendsData = costRepository.findMaintenanceTrendsByMonth(
            companyId, trendStartDateTime, endDateTime
        );

        // 월별 추세 데이터 구성
        List<MonthlyTrend> monthlyTrends = new ArrayList<>();
        BigDecimal previousAmount = null;

        for (MonthlyCostProjection trend : trendsData) {
            BigDecimal currentAmount = trend.getAmount() != null ? trend.getAmount() : BigDecimal.ZERO;
            BigDecimal changeRate = BigDecimal.ZERO;

            if (previousAmount != null && previousAmount.compareTo(BigDecimal.ZERO) > 0) {
                changeRate = currentAmount.subtract(previousAmount)
                    .divide(previousAmount, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            }

            // 해당 월의 주요 비용 유형 찾기 (간단히 "MAINTENANCE"로 설정)
            String primaryCostType = determinePrimaryCostType(companyId, trend.getMonth());

            MonthlyTrend monthlyTrend = MonthlyTrend.builder()
                .month(trend.getMonth())
                .totalAmount(currentAmount)
                .changeRate(changeRate)
                .primaryCostType(primaryCostType)
                .build();

            monthlyTrends.add(monthlyTrend);
            previousAmount = currentAmount;
        }

        // 전월 대비 총 증감률 계산
        BigDecimal totalChangeRate = BigDecimal.ZERO;
        if (monthlyTrends.size() >= 2) {
            MonthlyTrend current = monthlyTrends.get(monthlyTrends.size() - 1);
            MonthlyTrend previous = monthlyTrends.get(monthlyTrends.size() - 2);

            if (previous.getTotalAmount().compareTo(BigDecimal.ZERO) > 0) {
                totalChangeRate = current.getTotalAmount().subtract(previous.getTotalAmount())
                    .divide(previous.getTotalAmount(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            }
        }

        // 가장 증가/감소한 비용 유형 분석 (현재월 vs 전월)
        String mostIncreasedType = findMostChangedType(companyId, startDateTime, endDateTime, true);
        String mostDecreasedType = findMostChangedType(companyId, startDateTime, endDateTime, false);

        // 다음 달 예측 (단순 평균 기반)
        BigDecimal predictedNextMonth = BigDecimal.ZERO;
        BigDecimal predictionConfidence = BigDecimal.valueOf(75);

        if (!monthlyTrends.isEmpty()) {
            BigDecimal sum = BigDecimal.ZERO;
            for (MonthlyTrend trend : monthlyTrends) {
                sum = sum.add(trend.getTotalAmount());
            }
            predictedNextMonth = sum.divide(BigDecimal.valueOf(monthlyTrends.size()), 2, RoundingMode.HALF_UP);

            // 데이터가 많을수록 예측 신뢰도 증가
            if (monthlyTrends.size() >= 3) {
                predictionConfidence = BigDecimal.valueOf(80);
            }
            if (monthlyTrends.size() >= 6) {
                predictionConfidence = BigDecimal.valueOf(85);
            }
        }

        return MaintenanceTrends.builder()
            .monthlyTrends(monthlyTrends)
            .totalChangeRate(totalChangeRate)
            .mostIncreasedType(mostIncreasedType)
            .mostDecreasedType(mostDecreasedType)
            .predictedNextMonth(predictedNextMonth)
            .predictionConfidence(predictionConfidence)
            .build();
    }

    /**
     * 특정 월의 주요 비용 유형을 결정합니다.
     */
    private String determinePrimaryCostType(Long companyId, String month) {
        // 간단한 구현: 고정값 반환
        // 실제로는 해당 월의 가장 큰 비용 유형을 찾는 로직 구현 가능
        return "MAINTENANCE";
    }

    /**
     * 가장 증가/감소한 비용 유형을 찾습니다.
     */
    private String findMostChangedType(Long companyId, LocalDateTime startDateTime, LocalDateTime endDateTime, boolean findIncreased) {
        // 현재월과 전월의 비용 유형별 변화량을 분석하여
        // 가장 증가하거나 감소한 유형을 반환
        // 간단한 구현으로 기본값 반환
        if (findIncreased) {
            return "MAINTENANCE";
        } else {
            return "REPAIR";
        }
    }

    /**
     * 월 문자열을 날짜 범위로 파싱합니다.
     */
    private LocalDate[] parseMonthToDateRange(String month) {
        if (month == null || month.isEmpty()) {
            LocalDate now = LocalDate.now();
            return new LocalDate[]{now.withDayOfMonth(1), now.withDayOfMonth(now.lengthOfMonth())};
        }

        LocalDate date = LocalDate.parse(month + "-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return new LocalDate[]{date.withDayOfMonth(1), date.withDayOfMonth(date.lengthOfMonth())};
    }

    /**
     * 비용 유형의 표시명을 조회합니다.
     */
    private String getFeeTypeDisplayName(String feeTypeId) {
        return feeTypeConfig.getFeeTypeInfo(feeTypeId) != null ?
            feeTypeConfig.getFeeTypeInfo(feeTypeId).getDisplayName() :
            feeTypeId.toUpperCase();
    }

    /**
     * 비용 유형의 색상을 조회합니다.
     */
    private String getFeeTypeColor(String feeTypeId) {
        return feeTypeConfig.getFeeTypeInfo(feeTypeId) != null ?
            feeTypeConfig.getFeeTypeInfo(feeTypeId).getColor() :
            "#94A3B8";
    }

    // 헬퍼 메서드들
    private String determineCostLevel(BigDecimal total) {
        return "NORMAL"; // 평균 대비 계산 (향후 구현 예정)
    }

    private String findMajorCostType(Map<String, BigDecimal> costMap) {
        return costMap.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("maintenance");
    }

    private String findPeakMonth(List<MonthlyData> monthlyData) {
        return monthlyData.stream()
            .max(Comparator.comparing(MonthlyData::getTotal))
            .map(MonthlyData::getMonth)
            .orElse("");
    }

    private String findLowestMonth(List<MonthlyData> monthlyData) {
        return monthlyData.stream()
            .min(Comparator.comparing(MonthlyData::getTotal))
            .map(MonthlyData::getMonth)
            .orElse("");
    }

    private String findHighestCostVehicle(List<VehicleMaintenanceData> vehicles) {
        return vehicles.stream()
            .max(Comparator.comparing(VehicleMaintenanceData::getTotal))
            .map(VehicleMaintenanceData::getVehicleId)
            .orElse("");
    }

    private String findLowestCostVehicle(List<VehicleMaintenanceData> vehicles) {
        return vehicles.stream()
            .min(Comparator.comparing(VehicleMaintenanceData::getTotal))
            .map(VehicleMaintenanceData::getVehicleId)
            .orElse("");
    }
}