package com.group_7.library_management.dto;

/**
 * The counters displayed in the status section on the user's home screen.
 */
public record HomeSummaryResponse(
        long pendingPickupCount,
        long borrowingCount,
        long dueSoonCount,
        long overdueCount,
        long favoriteCount,
        long returnedCount,
        long allBorrowCount
) {
}
