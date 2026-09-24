import { apiClient } from "@/services/apiClient";
import type {
  Borrowing,
  BorrowRequest,
  BorrowingChartData,
  DashboardStats,
  OverdueBook,
} from "@/types/Borrowing";

export interface BorrowingFilters {
  search?: string;
  status?: string;
  page?: number;
  pageSize?: number;
}

export interface PaginatedResult<T> {
  items: T[];
  total: number;
  page: number;
  pageSize: number;
  totalPages: number;
}

export const borrowingService = {
  async getDashboardStats(): Promise<DashboardStats> {
    const summary = await apiClient.get<{ borrowingCount: number; overdueCount: number }>("/home/summary");
    return { totalBooks: 0, currentlyBorrowed: summary.borrowingCount, totalReaders: 0, overdueCount: summary.overdueCount };
  },

  async getBorrowingChartData(): Promise<BorrowingChartData[]> {
    return [];
  },

  async getRecentBorrowings(): Promise<Borrowing[]> {
    const items = await apiClient.get<ApiBorrowOrder[]>("/borrow-orders");
    return items.map(mapBorrowing).slice(0, 5);
  },

  async getOverdueBooks(): Promise<OverdueBook[]> {
    const items = await apiClient.get<ApiBorrowOrder[]>("/borrow-orders");
    return items.filter((item) => item.status.toLowerCase() === "overdue").map((item) => ({ id: item.referenceCode, readerName: item.borrowerName, bookTitle: item.bookTitle, dueDate: formatDate(item.dueAt), overdueDays: item.dueAt ? Math.max(0, Math.floor((Date.now() - new Date(item.dueAt).getTime()) / 86400000)) : 0 }));
  },

  async getBorrowings(filters: BorrowingFilters = {}): Promise<PaginatedResult<Borrowing>> {
    const page = filters.page ?? 1;
    const pageSize = filters.pageSize ?? 8;
    const orders = await apiClient.get<ApiBorrowOrder[]>("/borrow-orders");
    const query = filters.search?.toLowerCase();
    const filtered = orders.map(mapBorrowing).filter((item) =>
      (!query || [item.id, item.readerName, item.bookTitle, item.readerId].some((value) => value.toLowerCase().includes(query))) &&
      (!filters.status || item.status === filters.status)
    );
    const total = filtered.length;
    const totalPages = Math.max(1, Math.ceil(total / pageSize));
    const start = (page - 1) * pageSize;
    return { items: filtered.slice(start, start + pageSize), total, page, pageSize, totalPages };
  },

  async getBorrowingById(id: string): Promise<Borrowing | null> {
    try {
      return mapBorrowing(await apiClient.get<ApiBorrowOrder>(`/admin/borrow-orders/${encodeURIComponent(id)}`));
    } catch { return null; }
  },

  async searchForReturn(query: string): Promise<Borrowing | null> {
    try {
      const result = mapBorrowing(await apiClient.get<ApiBorrowOrder>(`/admin/borrow-orders/${encodeURIComponent(query.trim())}`));
      return result.status === "borrowing" || result.status === "overdue" ? result : null;
    } catch { return null; }
  },

  async confirmReturn(id: string): Promise<Borrowing | null> {
    try {
      return mapBorrowing(await apiClient.post<ApiBorrowOrder>(`/admin/borrow-orders/${encodeURIComponent(id)}/return`, {}));
    } catch { return null; }
  },

  async getBorrowRequests(): Promise<BorrowRequest[]> {
    throw new Error("Admin lấy danh sách yêu cầu mượn");
  },

  async approveRequest(id: string): Promise<BorrowRequest | null> {
    throw new Error(`Duyệt yêu cầu mượn (${id})`);
  },

  async rejectRequest(id: string): Promise<BorrowRequest | null> {
    throw new Error(`Từ chối yêu cầu mượn (${id})`);
  },
};

interface ApiBorrowOrder {
  id: number; referenceCode: string; status: string; bookId: number; bookTitle: string;
  borrowerId: number; borrowerName: string; requestedAt: string; borrowedAt?: string;
  dueAt?: string; returnedAt?: string;
}

function mapBorrowing(item: ApiBorrowOrder): Borrowing {
  return {
    id: item.referenceCode,
    readerId: String(item.borrowerId),
    readerName: item.borrowerName,
    bookId: String(item.bookId),
    bookTitle: item.bookTitle,
    borrowDate: formatDate(item.borrowedAt ?? item.requestedAt),
    dueDate: formatDate(item.dueAt),
    returnDate: formatDate(item.returnedAt),
    status: item.status.toLowerCase() as Borrowing["status"],
  };
}

function formatDate(value?: string): string {
  return value ? new Date(value).toLocaleDateString("vi-VN") : "-";
}
