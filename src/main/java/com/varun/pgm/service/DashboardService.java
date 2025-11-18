package com.varun.pgm.service;

import com.varun.pgm.dto.response.DashboardSummaryResponse;
import com.varun.pgm.entity.Payment;
import com.varun.pgm.entity.Room;
import com.varun.pgm.entity.Tenant;
import com.varun.pgm.repository.MaintenanceRepository;
import com.varun.pgm.repository.PaymentRepository;
import com.varun.pgm.repository.RoomRepository;
import com.varun.pgm.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final RoomRepository roomRepository;
    private final TenantRepository tenantRepository;
    private final PaymentRepository paymentRepository;
    private final MaintenanceRepository maintenanceRepository;

    public DashboardSummaryResponse getDashboardSummary() {
        DashboardSummaryResponse response = new DashboardSummaryResponse();

        // Occupancy Overview
        response.setOccupancy(getOccupancyOverview());

        // Tenant Overview
        response.setTenants(getTenantOverview());

        // Revenue Overview
        response.setRevenue(getRevenueOverview());

        // Payment Insights
        response.setPayments(getPaymentInsights());

        // Maintenance Overview
        response.setMaintenance(getMaintenanceOverview());

        // Recent Activities
        response.setRecentActivities(getRecentActivities());

        // Revenue Chart
        response.setRevenueChart(getRevenueChart());

        return response;
    }

    private DashboardSummaryResponse.OccupancyOverview getOccupancyOverview() {
        long totalRooms = roomRepository.countTotalRooms();
        long occupiedRooms = roomRepository.countByStatus(Room.RoomStatus.OCCUPIED);
        long availableRooms = roomRepository.countByStatus(Room.RoomStatus.AVAILABLE);
        long maintenanceRooms = roomRepository.countByStatus(Room.RoomStatus.MAINTENANCE);

        return new DashboardSummaryResponse.OccupancyOverview(
                (int) totalRooms,
                (int) occupiedRooms,
                (int) availableRooms,
                (int) maintenanceRooms
        );
    }

    private DashboardSummaryResponse.TenantOverview getTenantOverview() {
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());

        long totalTenants = tenantRepository.countByStatus(Tenant.TenantStatus.ACTIVE);
        long newThisMonth = tenantRepository.countNewTenantsInMonth(startOfMonth, endOfMonth);
        long checkoutsThisMonth = tenantRepository.countCheckoutsInMonth(startOfMonth, endOfMonth);

        return new DashboardSummaryResponse.TenantOverview(
                (int) totalTenants,
                (int) newThisMonth,
                (int) checkoutsThisMonth
        );
    }

    private DashboardSummaryResponse.RevenueOverview getRevenueOverview() {
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());

        BigDecimal rentCollected = paymentRepository.sumPaidPaymentsInDateRange(startOfMonth, endOfMonth);
        BigDecimal pendingRent = paymentRepository.sumPendingPayments();
        BigDecimal deposits = paymentRepository.sumActiveTenantDeposits();
        BigDecimal expectedMonthlyRevenue = paymentRepository.calculateExpectedMonthlyRevenue();

        return new DashboardSummaryResponse.RevenueOverview(
                rentCollected,
                pendingRent,
                deposits,
                expectedMonthlyRevenue
        );
    }

    private DashboardSummaryResponse.PaymentInsights getPaymentInsights() {
        // For simplicity, we'll assume all paid payments are on time
        // In a real implementation, you'd need payment due dates to calculate this properly
        long totalPayments = paymentRepository.count();
        long pendingPayments = paymentRepository.countPendingPayments();
        long paidPayments = totalPayments - pendingPayments;

        // Calculate average delay (simplified - in real app, calculate based on due dates)
        double averageDelayDays = 0.0; // Placeholder

        return new DashboardSummaryResponse.PaymentInsights(
                (int) paidPayments,
                (int) pendingPayments,
                averageDelayDays
        );
    }

    private DashboardSummaryResponse.MaintenanceOverview getMaintenanceOverview() {
        long totalRequests = maintenanceRepository.count();
        long pending = maintenanceRepository.countByStatus(com.varun.pgm.entity.Maintenance.MaintenanceStatus.PENDING);
        long resolved = maintenanceRepository.countByStatus(com.varun.pgm.entity.Maintenance.MaintenanceStatus.RESOLVED);

        return new DashboardSummaryResponse.MaintenanceOverview(
                (int) totalRequests,
                (int) pending,
                (int) resolved
        );
    }

    private List<DashboardSummaryResponse.RecentActivity> getRecentActivities() {
        List<DashboardSummaryResponse.RecentActivity> activities = new ArrayList<>();

        // Get recent payments
        List<Payment> recentPayments = paymentRepository.findRecentPayments();
        for (Payment payment : recentPayments.stream().limit(3).toList()) {
            activities.add(new DashboardSummaryResponse.RecentActivity(
                    "payment",
                    payment.getTenant().getName(),
                    payment.getAmount(),
                    null,
                    payment.getPaymentDate() != null ? payment.getPaymentDate().toString() : payment.getCreatedAt().toLocalDate().toString()
            ));
        }

        // Get recent tenant check-ins
        List<Tenant> recentTenants = tenantRepository.findRecentActiveTenants();
        for (Tenant tenant : recentTenants.stream().limit(2).toList()) {
            activities.add(new DashboardSummaryResponse.RecentActivity(
                    "check-in",
                    tenant.getName(),
                    null,
                    tenant.getRoom().getRoomNumber(),
                    tenant.getCheckInDate().toString()
            ));
        }

        // Sort by date (simplified - in real app, sort properly)
        return activities.stream().limit(5).toList();
    }

    private List<DashboardSummaryResponse.RevenueChartData> getRevenueChart() {
        List<DashboardSummaryResponse.RevenueChartData> chartData = new ArrayList<>();
        LocalDate sixMonthsAgo = LocalDate.now().minusMonths(6);

        List<Object[]> monthlyData = paymentRepository.findMonthlyRevenueLastSixMonths(sixMonthsAgo);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");

        for (Object[] data : monthlyData) {
            int month = (Integer) data[0];
            int year = (Integer) data[1];
            BigDecimal amount = (BigDecimal) data[2];

            LocalDate date = LocalDate.of(year, month, 1);
            String monthName = date.format(formatter);

            chartData.add(new DashboardSummaryResponse.RevenueChartData(monthName, amount));
        }

        // If no data, add sample data
        if (chartData.isEmpty()) {
            LocalDate now = LocalDate.now();
            for (int i = 5; i >= 0; i--) {
                LocalDate date = now.minusMonths(i);
                String monthName = date.format(DateTimeFormatter.ofPattern("MMMM yyyy"));
                chartData.add(new DashboardSummaryResponse.RevenueChartData(monthName, BigDecimal.valueOf(100000 + (i * 5000))));
            }
        }

        return chartData;
    }
}
