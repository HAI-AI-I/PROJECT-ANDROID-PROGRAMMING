import { initialHistory } from "@/data/history";
import type { HistoryRecord } from "@/types/History";

const store: HistoryRecord[] = [...initialHistory];
const delay = (ms = 300) => new Promise((r) => setTimeout(r, ms));

export interface HistoryFilters {
  search?: string;
  action?: string;
  dateFrom?: string;
  dateTo?: string;
}

export const historyService = {
  async getHistory(filters: HistoryFilters = {}): Promise<HistoryRecord[]> {
    await delay();
    let result = [...store];
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
