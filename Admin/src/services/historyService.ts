import { apiClient } from "@/services/apiClient";
import type { HistoryRecord } from "@/types/History";

export interface HistoryFilters {
  search?: string;
  action?: string;
  dateFrom?: string;
  dateTo?: string;
}

export const historyService = {
  async getHistory(filters: HistoryFilters = {}): Promise<HistoryRecord[]> {
    const items = await apiClient.get<ApiHistory[]>("/history");
    let result = items.map((item) => ({ id: String(item.id), action: item.type.toLowerCase() as HistoryRecord["action"], userName: `User #${item.userId}`, bookTitle: "", description: item.description, date: new Date(item.createdAt).toLocaleDateString("vi-VN") }));
    if (filters.search) {
      const q = filters.search.toLowerCase();
      result = result.filter(
        (h) =>
          h.userName.toLowerCase().includes(q) ||
          h.bookTitle.toLowerCase().includes(q)
      );
    }
    if (filters.action) result = result.filter((h) => h.action === filters.action);
    return result;
  },
};

interface ApiHistory { id: number; userId: number; type: string; description: string; createdAt: string; }
