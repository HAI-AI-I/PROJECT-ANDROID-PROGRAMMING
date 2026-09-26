"use client";

import { useCallback, useEffect, useState } from "react";
import DashboardContent from "@/components/admin/dashboard/DashboardContent";
import LoadingState from "@/components/ui/LoadingState";
import ErrorState from "@/components/ui/ErrorState";
import { analyticsService } from "@/services/analyticsService";
import type { Borrowing, BorrowingChartData, DashboardStats, OverdueBook } from "@/types/Borrowing";

export default function DashboardPage() {
  const [data, setData] = useState<{
    stats: DashboardStats;
    chartData: BorrowingChartData[];
    recentBorrowings: Borrowing[];
    overdueBooks: OverdueBook[];
  } | null>(null);
  const [error, setError] = useState(false);

  const loadDashboard = useCallback(async () => {
    setError(false);
    try {
      const dashboard = await analyticsService.getDashboard();
      setData({
        stats: {
          totalBooks: dashboard.totalBooks,
          currentlyBorrowed: dashboard.currentlyBorrowed,
          totalReaders: dashboard.totalReaders,
          overdueCount: dashboard.overdueCount,
        },
        chartData: dashboard.chartData,
        recentBorrowings: dashboard.recentBorrowings,
        overdueBooks: dashboard.overdueBooks,
      });
    } catch {
      setError(true);
    }
  }, []);

  useEffect(() => { loadDashboard(); }, [loadDashboard]);

  if (error) return <ErrorState onRetry={loadDashboard} />;
  if (!data) return <LoadingState />;

  return (
    <DashboardContent
      stats={data.stats}
      chartData={data.chartData}
      recentBorrowings={data.recentBorrowings}
      overdueBooks={data.overdueBooks}
    />
  );
}
