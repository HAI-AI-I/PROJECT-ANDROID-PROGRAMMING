import DashboardContent from "@/components/admin/dashboard/DashboardContent";
import { borrowingService } from "@/services/borrowingService";

export default async function DashboardPage() {
  const [stats, chartData, recentBorrowings, overdueBooks] = await Promise.all([
    borrowingService.getDashboardStats(),
    borrowingService.getBorrowingChartData(),
    borrowingService.getRecentBorrowings(),
    borrowingService.getOverdueBooks(),
  ]);

  return (
    <DashboardContent
      stats={stats}
      chartData={chartData}
      recentBorrowings={recentBorrowings}
      overdueBooks={overdueBooks}
    />
  );
}
