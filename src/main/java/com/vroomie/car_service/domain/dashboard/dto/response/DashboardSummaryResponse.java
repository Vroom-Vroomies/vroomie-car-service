package com.vroomie.car_service.domain.dashboard.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
@Jacksonized
public class DashboardSummaryResponse {

    private VehicleOwnership vehicleOwnership;
    private VehicleStatus vehicleStatus;
    private OperationalStats operationalStats;
    private List<MonthlyOperatingCost> monthlyOperatingCost;
    private MaintenanceCost maintenanceCost;
    private List<InefficiencyData> inefficiencyData;

    @Getter
    @Builder
    @Jacksonized
    public static class VehicleOwnership {
        private Integer total;
        private String centerLabel;
        private List<VehicleOwnershipItem> items;
    }

    @Getter
    @Builder
    @Jacksonized
    public static class VehicleOwnershipItem {
        private String label;
        private Integer value;
        private String color;
        private Integer percentage;
    }

    @Getter
    @Builder
    @Jacksonized
    public static class VehicleStatus {
        private Integer total;
        private String centerLabel;
        private List<VehicleStatusItem> items;
    }

    @Getter
    @Builder
    @Jacksonized
    public static class VehicleStatusItem {
        private String status;
        private Integer count;
        private String color;
        private Integer percentage;
    }

    @Getter
    @Builder
    @Jacksonized
    public static class OperationalStats {
        private List<OperationalStatsItem> items;
    }

    @Getter
    @Builder
    @Jacksonized
    public static class OperationalStatsItem {
        private String label;
        private Integer value;
        private String unit;
    }

    @Getter
    @Builder
    @Jacksonized
    public static class MonthlyOperatingCost {
        private String month;
        private BigDecimal fixedCost;
        private List<VariableCostItem> variableCost;
        private BigDecimal total;
        private BigDecimal previousMonthDiff;
    }

    @Getter
    @Builder
    @Jacksonized
    public static class VariableCostItem {
        private String feeTypeId;
        private BigDecimal amount;
        private String color;
    }

    @Getter
    @Builder
    @Jacksonized
    public static class MaintenanceCost {
        private BigDecimal total;
        private List<MaintenanceBreakdownItem> breakdown;
    }

    @Getter
    @Builder
    @Jacksonized
    public static class MaintenanceBreakdownItem {
        private String feeTypeId;
        private BigDecimal amount;
        private Integer percentage;
        private String color;
    }

    @Getter
    @Builder
    @Jacksonized
    public static class InefficiencyData {
        private String vehicleId;
        private String vehicleName;
        private Integer inefficiencyScore;
        private Integer rank;
        private String period;
        private Integer year;
        private Integer month;
        private String color;
    }
}