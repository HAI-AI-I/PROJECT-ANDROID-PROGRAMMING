package com.group_7.library_management.dto;

import java.util.List;

public record AdminDashboardResponse(
        long totalBooks,
        long currentlyBorrowed,
        long totalReaders,
        long overdueCount,
        List<ChartPoint> chartData,
        List<BorrowOrderResponse> recentBorrowings,
        List<BorrowOrderResponse> overdueBooks
) {
    public record ChartPoint(String day, long count) {}
}
