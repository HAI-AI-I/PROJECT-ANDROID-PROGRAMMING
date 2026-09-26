import type { Borrowing, OverdueBook } from "@/types/Borrowing";

export interface DashboardAnalytics {
  totalBooks: number;
  currentlyBorrowed: number;
  totalReaders: number;
  overdueCount: number;
  chartData: { day: string; count: number }[];
  recentBorrowings: Borrowing[];
  overdueBooks: OverdueBook[];
}

export interface AdminStatistics {
  totalBorrows: number;
  totalReturns: number;
  overdueBooks: number;
  borrowTrend: { month: string; count: number }[];
  returnTrend: { month: string; count: number }[];
  booksByCategory: { category: string; count: number }[];
  popularBooks: { title: string; count: number }[];
  activeReaders: { name: string; count: number }[];
}
