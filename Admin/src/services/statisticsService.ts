import { apiClient } from "@/services/apiClient";

export const statisticsService = {
  async getStatistics() {
    const [summary, borrowTrend, booksByCategory, popularBooks, activeReaders] = await Promise.all([
      apiClient.get<{ totalBorrowings: number; totalReturns: number; totalOverdue: number }>("/statistics"),
      apiClient.get<{ month: string; count: number }[]>("/statistics/borrowing-trend"),
      apiClient.get<{ name: string; value: number }[]>("/statistics/books-by-category"),
      apiClient.get<{ title: string; count: number }[]>("/statistics/popular-books"),
      apiClient.get<{ name: string; value: number }[]>("/statistics/active-readers"),
    ]);
    return {
      totalBorrows: summary.totalBorrowings,
      totalReturns: summary.totalReturns,
      overdueBooks: summary.totalOverdue,
      borrowTrend,
      returnTrend: borrowTrend,
      booksByCategory: booksByCategory.map((item) => ({ category: item.name, count: item.value })),
      popularBooks,
      activeReaders: activeReaders.map((item) => ({ name: item.name, count: item.value })),
    };
  },
};
