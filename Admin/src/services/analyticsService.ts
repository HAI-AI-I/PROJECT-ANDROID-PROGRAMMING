import { apiClient } from "@/services/apiClient";
import type { DashboardAnalytics } from "@/types/Analytics";
import type { Borrowing, BorrowingStatus } from "@/types/Borrowing";

export const analyticsService = {
  async getDashboard(): Promise<DashboardAnalytics> {
    const response = await apiClient.get<ApiDashboard>("/admin/analytics/dashboard");
    return {
      totalBooks: response.totalBooks,
      currentlyBorrowed: response.currentlyBorrowed,
      totalReaders: response.totalReaders,
      overdueCount: response.overdueCount,
      chartData: response.chartData,
      recentBorrowings: response.recentBorrowings.map(mapBorrowing),
      overdueBooks: response.overdueBooks.map((order) => ({
        id: order.referenceCode,
        readerName: order.borrowerName,
        bookTitle: order.bookTitle,
        dueDate: formatDate(order.dueAt),
        overdueDays: calculateOverdueDays(order.dueAt),
      })),
    };
  },
};

interface ApiDashboard {
  totalBooks: number;
  currentlyBorrowed: number;
  totalReaders: number;
  overdueCount: number;
  chartData: { day: string; count: number }[];
  recentBorrowings: ApiBorrowOrder[];
  overdueBooks: ApiBorrowOrder[];
}

interface ApiBorrowOrder {
  referenceCode: string;
  status: string;
  bookId: number;
  bookTitle: string;
  borrowerId: number;
  borrowerName: string;
  requestedAt: string;
  borrowedAt?: string;
  dueAt?: string;
  returnedAt?: string;
  copyBarcode: string;
}

function mapBorrowing(order: ApiBorrowOrder): Borrowing {
  const statuses: Record<string, BorrowingStatus> = {
    PENDING_PAYMENT: "pending_payment",
    REQUESTED: "requested",
    BORROWED: "borrowing",
    RETURNED: "returned",
    OVERDUE: "overdue",
    CANCELLED: "cancelled",
  };
  return {
    id: order.referenceCode,
    readerId: String(order.borrowerId),
    readerName: order.borrowerName,
    bookId: String(order.bookId),
    bookTitle: order.bookTitle,
    copyBarcode: order.copyBarcode,
    requestedDate: formatDate(order.requestedAt),
    borrowDate: formatDate(order.borrowedAt ?? order.requestedAt),
    dueDate: formatDate(order.dueAt),
    returnDate: order.returnedAt ? formatDate(order.returnedAt) : undefined,
    status: statuses[order.status] ?? "requested",
    overdueDays: calculateOverdueDays(order.dueAt),
  };
}

function calculateOverdueDays(value?: string): number {
  if (!value) return 0;
  return Math.max(0, Math.floor((Date.now() - new Date(value).getTime()) / 86_400_000));
}

function formatDate(value?: string): string {
  return value ? new Date(value).toLocaleDateString("vi-VN") : "-";
}
