package com.foodrescue.dto.response;

import lombok.Builder;
import lombok.Data;

/** Stats returned by the admin dashboard endpoint. */
@Data
@Builder
public class DashboardStatsResponse {
    private long totalUsers;
    private long totalDonors;
    private long totalNgos;
    private long totalVolunteers;
    private long totalDonations;
    private long availableDonations;
    private long completedDonations;
    private long expiredDonations;
    private long cancelledDonations;
    private double totalFoodRescuedKg;
    private long activePickups;
    private long pendingNgoApprovals;
}
