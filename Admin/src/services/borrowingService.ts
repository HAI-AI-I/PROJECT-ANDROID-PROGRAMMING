import { apiClient } from "@/services/apiClient";
import type {
  Borrowing,
  BorrowRequest,
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
  async getBorrowings(filters: BorrowingFilters = {}): Promise<PaginatedResult<Borrowing>> {
    const page = filters.page ?? 1;
    const pageSize = filters.pageSize ?? 8;
    const params = new URLSearchParams({ page: String(page), pageSize: String(pageSize) });
    if (filters.search) params.set("search", filters.search);
    if (filters.status) params.set("status", filters.status);
    const result = await apiClient.get<ApiPagedBorrowOrders>(`/admin/borrow-orders?${params}`);
    return { ...result, items: result.items.map(mapBorrowing) };
  },

  async getBorrowingById(id: string): Promise<Borrowing | null> {
    try {
      return mapBorrowing(await apiClient.get<ApiBorrowOrder>(`/admin/borrow-orders/${encodeURIComponent(id)}`));
    } catch { return null; }
  },

  async getReturnQueue(): Promise<Borrowing[]> {
    const statuses = ["borrowing", "overdue", "returned"];
    const results = await Promise.all(statuses.map((status) =>
      apiClient.get<ApiPagedBorrowOrders>(
        `/admin/borrow-orders?status=${status}&page=1&pageSize=100`
      )
    ));
    return results
      .flatMap((result) => result.items)
      .map(mapBorrowing)
      .filter((item) => item.status !== "returned" || (item.remainingRefundAmount ?? 0) > 0);
  },

  async searchForReturn(query: string): Promise<Borrowing> {
    const params = new URLSearchParams({ query: query.trim() });
    return mapBorrowing(await apiClient.get<ApiBorrowOrder>(
      `/admin/borrow-orders/return-search?${params}`
    ));
  },

  async confirmReturn(id: string): Promise<Borrowing> {
    return mapBorrowing(await apiClient.post<ApiBorrowOrder>(
      `/admin/borrow-orders/${encodeURIComponent(id)}/return`,
      {}
    ));
  },

  async confirmDepositRefund(id: string): Promise<Borrowing> {
    return mapBorrowing(await apiClient.post<ApiBorrowOrder>(
      `/admin/borrow-orders/${encodeURIComponent(id)}/refund-deposit`,
      {}
    ));
  },

  async getBorrowRequests(): Promise<BorrowRequest[]> {
    const result = await apiClient.get<ApiPagedBorrowOrders>(
      "/admin/borrow-orders?status=requested&page=1&pageSize=100"
    );
    return result.items.map(mapBorrowRequest);
  },

  async approveRequest(id: string): Promise<Borrowing> {
    const order = await apiClient.post<ApiBorrowOrder>(
      `/admin/borrow-orders/${encodeURIComponent(id)}/pickup`,
      {}
    );
    return mapBorrowing(order);
  },
};

interface ApiBorrowOrder {
  id: number; referenceCode: string; status: string; bookId: number; bookTitle: string;
  borrowerId: number; borrowerName: string; requestedAt: string; borrowedAt?: string;
  dueAt?: string; returnedAt?: string; copyBarcode: string;
  borrowFee: number; depositAmount: number; totalAmount: number; paidAmount: number;
  paymentStatus: string; paymentMethod?: string; depositRefunded: boolean;
  remainingRefundAmount: number;
}

interface ApiPagedBorrowOrders {
  items: ApiBorrowOrder[];
  total: number;
  page: number;
  pageSize: number;
  totalPages: number;
}

function mapBorrowing(item: ApiBorrowOrder): Borrowing {
  const statusMap: Record<string, Borrowing["status"]> = {
    PENDING_PAYMENT: "pending_payment",
    REQUESTED: "requested",
    BORROWED: "borrowing",
    RETURNED: "returned",
    OVERDUE: "overdue",
    CANCELLED: "cancelled",
  };
  return {
    id: item.referenceCode,
    readerId: String(item.borrowerId),
    readerName: item.borrowerName,
    bookId: String(item.bookId),
    bookTitle: item.bookTitle,
    copyBarcode: item.copyBarcode,
    requestedDate: formatDate(item.requestedAt),
    borrowDate: formatDate(item.borrowedAt ?? item.requestedAt),
    dueDate: formatDate(item.dueAt),
    returnDate: formatDate(item.returnedAt),
    status: statusMap[item.status] ?? "requested",
    borrowFee: item.borrowFee,
    depositAmount: item.depositAmount,
    totalAmount: item.totalAmount,
    paidAmount: item.paidAmount,
    paymentStatus: item.paymentStatus,
    paymentMethod: item.paymentMethod,
    depositRefunded: item.depositRefunded,
    remainingRefundAmount: item.remainingRefundAmount,
    overdueDays: item.dueAt && new Date(item.dueAt).getTime() < Date.now()
      ? Math.max(0, Math.floor((Date.now() - new Date(item.dueAt).getTime()) / 86400000))
      : 0,
  };
}

function mapBorrowRequest(item: ApiBorrowOrder): BorrowRequest {
  return {
    id: item.referenceCode,
    readerId: String(item.borrowerId),
    readerName: item.borrowerName,
    bookId: String(item.bookId),
    bookTitle: item.bookTitle,
    requestDate: formatDate(item.requestedAt),
    copyBarcode: item.copyBarcode,
    status: "requested",
  };
}

function formatDate(value?: string): string {
  return value ? new Date(value).toLocaleDateString("vi-VN") : "-";
}
