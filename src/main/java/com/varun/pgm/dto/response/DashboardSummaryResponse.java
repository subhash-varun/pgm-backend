package com.varun.pgm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryResponse {

    private OccupancyOverview occupancy;
    private TenantOverview tenants;
    private RevenueOverview revenue;
    private PaymentInsights payments;
    private MaintenanceOverview maintenance;
    private List<RecentActivity> recentActivities;
    private List<RevenueChartData> revenueChart;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OccupancyOverview {
        private int totalRooms;
        private int occupiedRooms;
        private int availableRooms;
        private int maintenanceRooms;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TenantOverview {
        private int total;
        private int newThisMonth;
        private int checkoutsThisMonth;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RevenueOverview {
        private BigDecimal rentCollected;
        private BigDecimal pendingRent;
        private BigDecimal deposits;
        private BigDecimal expectedMonthlyRevenue;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentInsights {
        private int onTime;
        private int late;
        private double averageDelayDays;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MaintenanceOverview {
        private int totalRequests;
        private int pending;
        private int resolved;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentActivity {
        private String type; // "payment" or "check-in"
        private String tenant;
        private BigDecimal amount; // for payments
        private String room; // for check-ins
        private String date;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RevenueChartData {
        private String month;
        private BigDecimal amount;
    }
}
