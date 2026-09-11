import {
  borrowingChartData,
  dashboardStats,
  initialBorrowRequests,
  overdueBooks,
  recentBorrowings,
} from "@/data/borrowings";
import type {
  Borrowing,
  BorrowRequest,
  BorrowingChartData,
  DashboardStats,
  OverdueBook,
} from "@/types/Borrowing";

let borrowingsStore: Borrowing[] = [...recentBorrowings];
let requestsStore: BorrowRequest[] = [...initialBorrowRequests];

const delay = (ms = 300) => new Promise((resolve) => setTimeout(resolve, ms));

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

function filterBorrowings(filters: BorrowingFilters): Borrowing[] {
  let result = [...borrowingsStore];
  if (filters.search) {
    const q = filters.search.toLowerCase();
    result = result.filter(
      (b) =>
        b.id.toLowerCase().includes(q) ||
        b.readerName.toLowerCase().includes(q) ||
        b.bookTitle.toLowerCase().includes(q) ||
        b.readerId.toLowerCase().includes(q)
    );
  }
  if (filters.status) result = result.filter((b) => b.status === filters.status);
  return result;
}

export const borrowingService = {
  async getDashboardStats(): Promise<DashboardStats> {
    await delay();
    return dashboardStats;
  },

  async getBorrowingChartData(): Promise<BorrowingChartData[]> {
    await delay();
    return borrowingChartData;
  },

  async getRecentBorrowings(): Promise<Borrowing[]> {
    await delay();
    return borrowingsStore.slice(0, 5);
  },

  async getOverdueBooks(): Promise<OverdueBook[]> {
    await delay();
    return overdueBooks;
  },

  async getBorrowings(filters: BorrowingFilters = {}): Promise<PaginatedResult<Borrowing>> {
    await delay();
    const page = filters.page ?? 1;
    const pageSize = filters.pageSize ?? 8;
    const filtered = filterBorrowings(filters);
    const total = filtered.length;
    const totalPages = Math.max(1, Math.ceil(total / pageSize));
    const start = (page - 1) * pageSize;
    return { items: filtered.slice(start, start + pageSize), total, page, pageSize, totalPages };
  },

  async getBorrowingById(id: string): Promise<Borrowing | null> {
    await delay();
    return borrowingsStore.find((b) => b.id === id || b.readerId === id) ?? null;
  },

  async searchForReturn(query: string): Promise<Borrowing | null> {
    await delay();
    const q = query.trim().toLowerCase();
    return (
      borrowingsStore.find(
        (b) =>
          (b.id.toLowerCase() === q || b.readerId.toLowerCase() === q) &&
          (b.status === "borrowing" || b.status === "overdue")
      ) ?? null
    );
  },

  async confirmReturn(id: string): Promise<Borrowing | null> {
    await delay();
    const idx = borrowingsStore.findIndex((b) => b.id === id);
    if (idx === -1) return null;
    borrowingsStore[idx] = {
      ...borrowingsStore[idx],
      status: "returned",
      returnDate: new Date().toLocaleDateString("vi-VN"),
    };
    return borrowingsStore[idx];
  },

  async getBorrowRequests(): Promise<BorrowRequest[]> {
    await delay();
    return requestsStore.filter((r) => r.status === "pending");
  },

  async approveRequest(id: string): Promise<BorrowRequest | null> {
    await delay();
    const idx = requestsStore.findIndex((r) => r.id === id);
    if (idx === -1) return null;
    const req = requestsStore[idx];
    requestsStore[idx] = { ...req, status: "approved" };
    borrowingsStore = [
      {
        id: `BR-${String(borrowingsStore.length + 1).padStart(3, "0")}`,
        readerId: req.readerId,
        readerName: req.readerName,
        bookId: req.bookId,
        bookTitle: req.bookTitle,
        borrowDate: new Date().toLocaleDateString("vi-VN"),
        dueDate: new Date(Date.now() + 14 * 86400000).toLocaleDateString("vi-VN"),
        status: "borrowing",
      },
      ...borrowingsStore,
    ];
    return requestsStore[idx];
  },

  async rejectRequest(id: string): Promise<BorrowRequest | null> {
    await delay();
    const idx = requestsStore.findIndex((r) => r.id === id);
    if (idx === -1) return null;
    requestsStore[idx] = { ...requestsStore[idx], status: "rejected" };
    return requestsStore[idx];
  },
};
