import { apiClient } from "@/services/apiClient";
import type { AdminStatistics } from "@/types/Analytics";

export const statisticsService = {
  async getStatistics(): Promise<AdminStatistics> {
    const response = await apiClient.get<ApiStatistics>("/admin/analytics/statistics");
    return {
      totalBorrows: response.totalBorrows,
      totalReturns: response.totalReturns,
      overdueBooks: response.overdueBooks,
      borrowTrend: response.borrowTrend,
      returnTrend: response.returnTrend,
      booksByCategory: response.booksByCategory,
      popularBooks: response.popularBooks.map((item) => ({ title: item.name, count: item.count })),
      activeReaders: response.activeReaders,
    };
  },
};

interface ApiStatistics {
  totalBorrows: number;
  totalReturns: number;
  overdueBooks: number;
  borrowTrend: { month: string; count: number }[];
  returnTrend: { month: string; count: number }[];
  booksByCategory: { category: string; count: number }[];
  popularBooks: { name: string; count: number }[];
  activeReaders: { name: string; count: number }[];
}
