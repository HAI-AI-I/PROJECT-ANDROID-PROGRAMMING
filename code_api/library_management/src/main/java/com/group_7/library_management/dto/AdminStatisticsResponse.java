package com.group_7.library_management.dto;

import java.util.List;

public record AdminStatisticsResponse(
        long totalBorrows,
        long totalReturns,
        long overdueBooks,
        List<TrendPoint> borrowTrend,
        List<TrendPoint> returnTrend,
        List<CategoryPoint> booksByCategory,
        List<RankedPoint> popularBooks,
        List<RankedPoint> activeReaders
) {
    public record TrendPoint(String month, long count) {}
    public record CategoryPoint(String category, long count) {}
    public record RankedPoint(String name, long count) {}
}
