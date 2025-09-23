package com.vroomie.car_service.domain.dashboard.service;

import com.vroomie.car_service.domain.dashboard.config.FeeTypeConfig;
import com.vroomie.car_service.domain.dashboard.dto.FeeTypeInfo;
import com.vroomie.car_service.domain.dashboard.dto.response.FeeTypeResponse;
import com.vroomie.car_service.domain.dashboard.dto.response.FeeTypeResponse.ChartSettings;
import com.vroomie.car_service.domain.dashboard.dto.response.FeeTypeResponse.FeeTypeData;
import com.vroomie.car_service.domain.dashboard.dto.response.FeeTypeResponse.FeeTypeStatistics;
import com.vroomie.car_service.domain.dashboard.projection.AvailableFeeTypeProjection;
import com.vroomie.car_service.domain.dashboard.repository.DashboardContractRepository;
import com.vroomie.car_service.domain.dashboard.repository.DashboardCostRepository;
import com.vroomie.car_service.domain.dashboard.repository.DashboardInsuranceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * 동적 비용 유형 서비스
 * 실제 데이터에서 사용 중인 비용 유형을 발견하고 관리하는 서비스입니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DynamicFeeTypeService {

    private final DashboardCostRepository costRepository;
    private final DashboardContractRepository contractRepository;
    private final DashboardInsuranceRepository insuranceRepository;
    private final FeeTypeConfig feeTypeConfig;

    /**
     * 사용 가능한 비용 유형을 조회합니다.
     * 실제 데이터에서 사용 중인 비용 유형들을 발견하여 메타데이터와 함께 반환합니다.
     *
     * @param companyId 회사 ID
     * @return 사용 가능한 비용 유형 응답 데이터
     */
    public FeeTypeResponse getAvailableFeeTypes(Long companyId) {
        log.info("[DynamicFeeTypeService] 사용 가능한 비용 유형 조회 시작 - companyId: {}", companyId);

        // 실제 데이터에서 사용 중인 비용 유형 조회
        List<String> availableFeeTypes = new ArrayList<>();
        Map<String, BigDecimal> feeTypeAmounts = new HashMap<>();

        // 변동 비용에서 사용 중인 유형
        List<AvailableFeeTypeProjection> variableFeeTypes = costRepository.findAvailableVariableFeeTypes(companyId);
        for (AvailableFeeTypeProjection feeType : variableFeeTypes) {
            availableFeeTypes.add(feeType.getFeeType());
            feeTypeAmounts.put(feeType.getFeeType(), feeType.getTotalAmount());
        }

        // 계약에서 사용 중인 유형
        List<AvailableFeeTypeProjection> contractFeeTypes = contractRepository.findAvailableContractFeeTypes(companyId);
        for (AvailableFeeTypeProjection feeType : contractFeeTypes) {
            availableFeeTypes.add(feeType.getFeeType());
            feeTypeAmounts.put(feeType.getFeeType(), feeType.getTotalAmount());
        }

        // 보험에서 사용 중인 유형
        List<AvailableFeeTypeProjection> insuranceFeeTypes = insuranceRepository.findAvailableInsuranceFeeTypes(companyId);
        for (AvailableFeeTypeProjection feeType : insuranceFeeTypes) {
            availableFeeTypes.add(feeType.getFeeType());
            feeTypeAmounts.put(feeType.getFeeType(), feeType.getTotalAmount());
        }

        // 중복 제거
        Set<String> uniqueFeeTypes = new HashSet<>(availableFeeTypes);

        // 총 금액 계산
        BigDecimal totalAmount = feeTypeAmounts.values().stream()
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 설정 매핑 및 FeeTypeData 생성
        List<FeeTypeData> feeTypes = new ArrayList<>();
        for (String feeTypeId : uniqueFeeTypes) {
            FeeTypeInfo config = feeTypeConfig.getFeeTypeInfo(feeTypeId);
            BigDecimal amount = feeTypeAmounts.get(feeTypeId);
            BigDecimal percentage = totalAmount.compareTo(BigDecimal.ZERO) > 0 ?
                amount.divide(totalAmount, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)) :
                BigDecimal.ZERO;

            if (config != null) {
                feeTypes.add(toFeeTypeData(config, amount, percentage));
            } else {
                feeTypes.add(createDefaultFeeTypeData(feeTypeId, amount, percentage));
            }
        }

        // 정렬 순서대로 정렬
        feeTypes.sort(Comparator.comparing(FeeTypeData::getSortOrder));

        // 차트 설정 생성
        ChartSettings chartSettings = createChartSettings();

        // 통계 정보 생성
        FeeTypeStatistics statistics = createStatistics(feeTypes, totalAmount);

        log.info("[DynamicFeeTypeService] 사용 가능한 비용 유형 조회 완료 - {}개 유형 발견", feeTypes.size());

        return FeeTypeResponse.builder()
            .feeTypes(feeTypes)
            .chartSettings(chartSettings)
            .statistics(statistics)
            .build();
    }

    /**
     * 회사별로 사용 중인 모든 비용 유형의 통계를 조회합니다.
     *
     * @param companyId 회사 ID
     * @return 비용 유형 통계 정보
     */
    public FeeTypeStatistics getFeeTypeStatistics(Long companyId) {
        log.info("[DynamicFeeTypeService] 비용 유형 통계 조회 시작 - companyId: {}", companyId);

        FeeTypeResponse response = getAvailableFeeTypes(companyId);
        return response.getStatistics();
    }

    /**
     * 특정 비용 유형의 상세 정보를 조회합니다.
     *
     * @param feeTypeId 비용 유형 ID
     * @return 비용 유형 상세 정보
     */
    public FeeTypeData getFeeTypeDetail(String feeTypeId) {
        log.info("[DynamicFeeTypeService] 비용 유형 상세 조회 - feeTypeId: {}", feeTypeId);

        FeeTypeInfo config = feeTypeConfig.getFeeTypeInfo(feeTypeId);

        if (config != null) {
            return toFeeTypeData(config, BigDecimal.ZERO, BigDecimal.ZERO);
        } else {
            return createDefaultFeeTypeData(feeTypeId, BigDecimal.ZERO, BigDecimal.ZERO);
        }
    }

    /**
     * FeeTypeInfo를 FeeTypeData로 변환합니다.
     */
    private FeeTypeData toFeeTypeData(FeeTypeInfo config, BigDecimal amount, BigDecimal percentage) {
        return FeeTypeData.builder()
            .id(config.getFeeTypeId())
            .name(config.getFeeTypeId())
            .displayName(config.getDisplayName())
            .color(config.getColor())
            .category(config.getCategory().name())
            .isActive(true)
            .sortOrder(config.getSortOrder())
            .icon(config.getIcon())
            .description(config.getDescription())
            .totalAmount(amount)
            .percentage(percentage)
            .source("config") // 설정에서 가져온 데이터
            .build();
    }

    /**
     * 기본 비용 유형 데이터를 생성합니다.
     * 설정에 없는 비용 유형에 대한 기본값을 제공합니다.
     */
    private FeeTypeData createDefaultFeeTypeData(String feeTypeId, BigDecimal amount, BigDecimal percentage) {
        String displayName = formatDisplayName(feeTypeId);
        String category = inferCategory(feeTypeId);
        String color = getDefaultColor(category);

        return FeeTypeData.builder()
            .id(feeTypeId)
            .name(feeTypeId)
            .displayName(displayName)
            .color(color)
            .category(category)
            .isActive(true)
            .sortOrder(999) // 기본값은 맨 뒤로
            .icon("💰")
            .description(displayName + " 관련 비용")
            .totalAmount(amount)
            .percentage(percentage)
            .source("discovered") // 데이터에서 발견된 것
            .build();
    }

    /**
     * 차트 설정을 생성합니다.
     */
    private ChartSettings createChartSettings() {
        List<String> colorPalette = Arrays.asList(
            "#1E40AF", "#3B82F6", "#60A5FA", "#93C5FD",
            "#8B5CF6", "#A78BFA", "#C4B5FD", "#DDD6FE",
            "#10B981", "#34D399", "#6EE7B7", "#9DECCD",
            "#F59E0B", "#FBBF24", "#FCD34D", "#FDE68A",
            "#EF4444", "#F87171", "#FCA5A5", "#FECACA"
        );

        return ChartSettings.builder()
            .colorPalette(colorPalette)
            .defaultView("donut")
            .enableAnimation(true)
            .legendPosition("bottom")
            .tooltipFormat("amount")
            .build();
    }

    /**
     * 비용 유형 통계 정보를 생성합니다.
     */
    private FeeTypeStatistics createStatistics(List<FeeTypeData> feeTypes, BigDecimal totalAmount) {
        int totalCount = feeTypes.size();
        int activeCount = (int) feeTypes.stream().filter(FeeTypeData::getIsActive).count();

        Map<String, Long> categoryCounts = new HashMap<>();
        for (FeeTypeData feeType : feeTypes) {
            categoryCounts.merge(feeType.getCategory(), 1L, Long::sum);
        }

        // 지배적인 비용 유형 찾기
        FeeTypeData dominantType = feeTypes.stream()
            .max(Comparator.comparing(FeeTypeData::getTotalAmount))
            .orElse(null);

        BigDecimal averageAmount = totalCount > 0 ?
            totalAmount.divide(BigDecimal.valueOf(totalCount), 2, RoundingMode.HALF_UP) :
            BigDecimal.ZERO;

        return FeeTypeStatistics.builder()
            .totalFeeTypes(totalCount)
            .activeFeeTypes(activeCount)
            .fixedCostTypes(categoryCounts.getOrDefault("FIXED", 0L).intValue())
            .variableCostTypes(categoryCounts.getOrDefault("VARIABLE", 0L).intValue())
            .maintenanceCostTypes(categoryCounts.getOrDefault("MAINTENANCE", 0L).intValue())
            .dominantFeeType(dominantType != null ? dominantType.getId() : "")
            .dominantFeeTypeRatio(dominantType != null ? dominantType.getPercentage() : BigDecimal.ZERO)
            .totalAmount(totalAmount)
            .averageAmountPerType(averageAmount)
            .build();
    }

    /**
     * 표시명을 포맷팅합니다.
     */
    private String formatDisplayName(String feeTypeId) {
        if (feeTypeId == null || feeTypeId.isEmpty()) {
            return "기타";
        }

        // 언더스코어를 공백으로 변경하고 첫 글자를 대문자로
        return Arrays.stream(feeTypeId.split("_"))
            .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
            .reduce((a, b) -> a + " " + b)
            .orElse(feeTypeId);
    }

    /**
     * 비용 유형 ID로부터 카테고리를 추론합니다.
     */
    private String inferCategory(String feeTypeId) {
        if (feeTypeId == null) return "OPERATIONAL";

        String lowerFeeTypeId = feeTypeId.toLowerCase();

        if (lowerFeeTypeId.contains("contract") || lowerFeeTypeId.contains("insurance") ||
            lowerFeeTypeId.contains("lease") || lowerFeeTypeId.contains("rent")) {
            return "FIXED";
        } else if (lowerFeeTypeId.contains("maintenance") || lowerFeeTypeId.contains("repair") ||
                   lowerFeeTypeId.contains("inspection")) {
            return "MAINTENANCE";
        } else if (lowerFeeTypeId.contains("fuel") || lowerFeeTypeId.contains("gas") ||
                   lowerFeeTypeId.contains("toll")) {
            return "VARIABLE";
        } else {
            return "OPERATIONAL";
        }
    }

    /**
     * 카테고리에 따른 기본 색상을 반환합니다.
     */
    private String getDefaultColor(String category) {
        switch (category) {
            case "FIXED": return "#1E40AF";
            case "VARIABLE": return "#3B82F6";
            case "MAINTENANCE": return "#10B981";
            default: return "#94A3B8";
        }
    }
}