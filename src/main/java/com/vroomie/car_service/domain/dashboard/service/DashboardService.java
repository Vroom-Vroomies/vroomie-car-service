package com.vroomie.car_service.domain.dashboard.service;

import com.vroomie.car_service.domain.dashboard.repository.DashboardCarRepository;
import com.vroomie.car_service.domain.dashboard.repository.DashboardOperationRepository;
import com.vroomie.car_service.domain.dashboard.projection.CarTypeDistributionProjection;
import com.vroomie.car_service.domain.dashboard.projection.CarStatusProjection;
import com.vroomie.car_service.domain.dashboard.projection.OperationalStatsProjection;
import com.vroomie.car_service.domain.dashboard.dto.response.CarTypeDistributionResponse;
import com.vroomie.car_service.domain.dashboard.dto.response.CarTypeDistributionResponse.CarTypeDistributionItem;
import com.vroomie.car_service.domain.dashboard.dto.response.CarStatusResponse;
import com.vroomie.car_service.domain.dashboard.dto.response.CarStatusResponse.CarStatusItem;
import com.vroomie.car_service.domain.dashboard.dto.response.OperationalStatsResponse;
import com.vroomie.car_service.domain.dashboard.dto.response.OperationalStatsResponse.OperationalStatsItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final DashboardCarRepository dashboardCarRepository;
    private final DashboardOperationRepository dashboardOperationRepository;

    // 동적 차량 타입을 위한 색상 팔레트 (순서대로 할당)
    private static final List<String> CAR_TYPE_COLOR_PALETTE = Arrays.asList(
        "#1E40AF", "#60A5FA", "#93C5FD", "#DBEAFE",
        "#DC2626", "#F87171", "#FCA5A5", "#FEE2E2",
        "#059669", "#34D399", "#86EFAC", "#D1FAE5",
        "#7C3AED", "#A78BFA", "#C4B5FD", "#EDE9FE"
    );

    private static final Map<String, String> CAR_STATUS_COLORS = Map.of(
        "정상", "#1E40AF",
        "점검중", "#60A5FA",
        "수리중", "#93C5FD",
        "보험 만료", "#DBEAFE"
    );

    /**
     * 동적 차량 타입에 색상을 할당하는 메서드
     * @param carType 차량 타입
     * @param index 색상 인덱스
     * @return 할당된 색상 코드
     */
    private String getCarTypeColor(String carType, int index) {
        return CAR_TYPE_COLOR_PALETTE.get(index % CAR_TYPE_COLOR_PALETTE.size());
    }

    /**
     * 회사별 차량 타입별 분포 현황을 조회합니다.
     * 차량 종류(세단, 트럭, SUV 등)별 보유 대수와 비율을 반환합니다.
     *
     * @param companyId 회사 ID
     * @return CarTypeDistributionResponse 차량 타입별 분포 응답 DTO
     */
    public CarTypeDistributionResponse getCarTypeDistribution(Long companyId) {
        List<CarTypeDistributionProjection> projections = dashboardCarRepository.findCarTypeDistributionByCompany(companyId);
        Long totalCount = dashboardCarRepository.countByCompanyId(companyId);

        AtomicInteger colorIndex = new AtomicInteger(0);
        List<CarTypeDistributionItem> items = projections.stream()
            .map(p -> CarTypeDistributionItem.builder()
                .label(p.getType())
                .value(p.getCount().intValue())
                .color(getCarTypeColor(p.getType(), colorIndex.getAndIncrement()))
                .percentage(totalCount > 0 ? (p.getCount().doubleValue() / totalCount * 100) : 0.0)
                .build())
            .collect(Collectors.toList());

        return CarTypeDistributionResponse.builder()
            .total(totalCount.intValue())
            .centerLabel("대")
            .items(items)
            .build();
    }

    /**
     * 회사별 차량 상태 분포를 조회합니다.
     * 차량 상태(정상, 점검중, 수리중, 보험 만료)별 대수와 비율을 반환합니다.
     *
     * @param companyId 회사 ID
     * @return CarStatusResponse 차량 상태 분포 응답 DTO
     */
    public CarStatusResponse getCarStatus(Long companyId) {
        List<CarStatusProjection> projections = dashboardCarRepository.findCarStatusByCompany(companyId);

        int runningTotal = projections.stream()
            .filter(p -> "정상".equals(p.getStatus()))
            .mapToInt(p -> p.getCount().intValue())
            .sum();

        int totalCount = projections.stream()
            .mapToInt(p -> p.getCount().intValue())
            .sum();

        List<CarStatusItem> items = projections.stream()
            .map(p -> CarStatusItem.builder()
                .status(p.getStatus())
                .count(p.getCount().intValue())
                .color(CAR_STATUS_COLORS.getOrDefault(p.getStatus(), "#94A3B8"))
                .percentage(totalCount > 0 ? (p.getCount().doubleValue() / totalCount * 100) : 0.0)
                .build())
            .collect(Collectors.toList());

        return CarStatusResponse.builder()
            .total(runningTotal)
            .centerLabel("정상")
            .items(items)
            .build();
    }

    /**
     * 회사별 운영 통계를 조회합니다.
     * 운행기록 미작성건, 지급된 차량, 대여중 차량, 반납지연 차량 수를 반환합니다.
     *
     * @param companyId 회사 ID
     * @return OperationalStatsResponse 운영 통계 응답 DTO
     */
    public OperationalStatsResponse getOperationalStats(Long companyId) {
        OperationalStatsProjection projection = dashboardOperationRepository.findOperationalStatsByCompany(companyId);

        List<OperationalStatsItem> items = Arrays.asList(
            OperationalStatsItem.builder()
                .label("운행 기록 미 작성건")
                .value(projection.getUnwrittenDrivingLogs().intValue())
                .unit("건")
                .build(),
            OperationalStatsItem.builder()
                .label("지급 된 차량")
                .value(projection.getAssignedVehicles().intValue())
                .unit("대")
                .build(),
            OperationalStatsItem.builder()
                .label("대여 중 차량")
                .value(projection.getRentedVehicles().intValue())
                .unit("대")
                .build(),
            OperationalStatsItem.builder()
                .label("반납 지연 차량")
                .value(projection.getOverdueVehicles().intValue())
                .unit("대")
                .build()
        );

        return OperationalStatsResponse.builder()
            .items(items)
            .build();
    }
}