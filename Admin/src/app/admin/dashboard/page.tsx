"use client";

import { useCallback, useEffect, useState } from "react";
import DashboardContent from "@/components/admin/dashboard/DashboardContent";
import LoadingState from "@/components/ui/LoadingState";
import ErrorState from "@/components/ui/ErrorState";
import { borrowingService } from "@/services/borrowingService";
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
      const [stats, chartData, recentBorrowings, overdueBooks] = await Promise.all([
        borrowingService.getDashboardStats(),
        borrowingService.getBorrowingChartData(),
        borrowingService.getRecentBorrowings(),
        borrowingService.getOverdueBooks(),
      ]);
      setData({ stats, chartData, recentBorrowings, overdueBooks });
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
