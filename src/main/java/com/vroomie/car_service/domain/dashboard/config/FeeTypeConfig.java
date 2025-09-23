package com.vroomie.car_service.domain.dashboard.config;

import com.vroomie.car_service.domain.dashboard.dto.FeeTypeInfo;
import com.vroomie.car_service.domain.dashboard.dto.FeeTypeInfo.FeeTypeCategory;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * 동적 비용 유형 설정 관리 컴포넌트
 * 기본 비용 유형들의 메타데이터(색상, 아이콘, 카테고리 등)를 관리합니다.
 * 데이터베이스 테이블 없이 설정 기반으로 비용 유형 정보를 제공합니다.
 */
@Component
public class FeeTypeConfig {

    /**
     * 기본 비용 유형 설정 맵
     * 계약료, 보험료, 연료비, 정비비 등의 기본 설정을 포함합니다.
     */
    private static final Map<String, FeeTypeInfo> DEFAULT_FEE_TYPES = Map.of(
        "contract", FeeTypeInfo.builder()
            .feeTypeId("contract")
            .displayName("계약료")
            .color("#1E40AF")
            .category(FeeTypeCategory.FIXED)
            .icon("📄")
            .description("차량 계약 관련 비용")
            .sortOrder(1)
            .build(),
        "insurance", FeeTypeInfo.builder()
            .feeTypeId("insurance")
            .displayName("보험료")
            .color("#8B5CF6")
            .category(FeeTypeCategory.FIXED)
            .icon("🛡️")
            .description("차량 보험 관련 비용")
            .sortOrder(2)
            .build(),
        "fuel", FeeTypeInfo.builder()
            .feeTypeId("fuel")
            .displayName("연료비")
            .color("#3B82F6")
            .category(FeeTypeCategory.VARIABLE)
            .icon("⛽")
            .description("연료 관련 비용")
            .sortOrder(3)
            .build(),
        "maintenance", FeeTypeInfo.builder()
            .feeTypeId("maintenance")
            .displayName("정비비")
            .color("#10B981")
            .category(FeeTypeCategory.MAINTENANCE)
            .icon("🔧")
            .description("차량 정비 관련 비용")
            .sortOrder(4)
            .build()
    );

    /**
     * 특정 비용 유형 ID에 대한 설정 정보를 조회합니다.
     *
     * @param feeTypeId 조회할 비용 유형 ID
     * @return 비용 유형 정보, 존재하지 않으면 null
     */
    public FeeTypeInfo getFeeTypeInfo(String feeTypeId) {
        return DEFAULT_FEE_TYPES.get(feeTypeId);
    }

    /**
     * 모든 기본 비용 유형들을 정렬 순서대로 조회합니다.
     *
     * @return 정렬된 비용 유형 정보 리스트
     */
    public List<FeeTypeInfo> getAllFeeTypes() {
        return DEFAULT_FEE_TYPES.values().stream()
            .sorted(Comparator.comparing(FeeTypeInfo::getSortOrder))
            .toList();
    }
}