import { apiClient } from "@/services/apiClient";
import type { HistoryRecord } from "@/types/History";

export interface HistoryFilters {
  search?: string;
  action?: string;
  page?: number;
  pageSize?: number;
}

export interface PaginatedHistory {
  items: HistoryRecord[];
  total: number;
  page: number;
  pageSize: number;
  totalPages: number;
}

export const historyService = {
  async getHistory(filters: HistoryFilters = {}): Promise<PaginatedHistory> {
    const params = new URLSearchParams({
      page: String(filters.page ?? 1),
      pageSize: String(filters.pageSize ?? 10),
    });
    if (filters.search?.trim()) params.set("search", filters.search.trim());
    if (filters.action) params.set("action", filters.action);

    const response = await apiClient.get<ApiPagedHistory>(`/admin/history?${params}`);
    return {
      ...response,
      items: response.items.map((item) => ({
        id: item.id,
        action: item.action as HistoryRecord["action"],
        userName: item.userName,
        bookTitle: item.bookTitle,
        referenceCode: item.referenceCode,
        description: item.description,
        date: new Date(item.occurredAt).toLocaleString("vi-VN"),
      })),
    };
  },
};

interface ApiHistory {
  id: string;
  action: string;
  userName: string;
  bookTitle: string;
  referenceCode: string;
  description: string;
  occurredAt: string;
}

interface ApiPagedHistory {
  items: ApiHistory[];
  total: number;
  page: number;
  pageSize: number;
  totalPages: number;
}
