import { initialReaders } from "@/data/readers";
import type { Reader, ReaderFormData } from "@/types/Reader";

let store: Reader[] = [...initialReaders];
const delay = (ms = 300) => new Promise((r) => setTimeout(r, ms));

export interface PaginatedResult<T> {
  items: T[];
  total: number;
  page: number;
  pageSize: number;
  totalPages: number;
}

export interface ReaderFilters {
  search?: string;
  status?: string;
  page?: number;
  pageSize?: number;
}

function filter(filters: ReaderFilters): Reader[] {
  let result = [...store];
  if (filters.search) {
    const q = filters.search.toLowerCase();
    result = result.filter(
      (r) =>
        r.name.toLowerCase().includes(q) ||
        r.email.toLowerCase().includes(q) ||
        r.readerId.toLowerCase().includes(q) ||
        r.phone.includes(q)
    );
  }
  if (filters.status) result = result.filter((r) => r.status === filters.status);
  return result;
}

export const readerService = {
  async getReaders(filters: ReaderFilters = {}): Promise<PaginatedResult<Reader>> {
    await delay();
    const page = filters.page ?? 1;
    const pageSize = filters.pageSize ?? 8;
    const filtered = filter(filters);
    const total = filtered.length;
    const totalPages = Math.max(1, Math.ceil(total / pageSize));
    const start = (page - 1) * pageSize;
    return { items: filtered.slice(start, start + pageSize), total, page, pageSize, totalPages };
  },

  async getReaderById(id: string): Promise<Reader | null> {
    await delay();
    return store.find((r) => r.readerId === id) ?? null;
  },

  async createReader(data: ReaderFormData): Promise<Reader> {
    await delay();
    const reader: Reader = {
      readerId: `R-${String(store.length + 1).padStart(3, "0")}`,
      name: data.name.trim(),
      email: data.email.trim(),
      phone: data.phone.trim(),
      booksBorrowing: 0,
      status: data.status,
      registeredDate: new Date().toLocaleDateString("vi-VN"),
    };
    store = [reader, ...store];
    return reader;
  },

  async updateReader(id: string, data: ReaderFormData): Promise<Reader | null> {
    await delay();
    const idx = store.findIndex((r) => r.readerId === id);
    if (idx === -1) return null;
    store[idx] = { ...store[idx], ...data, name: data.name.trim(), email: data.email.trim(), phone: data.phone.trim() };
    return store[idx];
  },

  async deleteReader(id: string): Promise<boolean> {
    await delay();
    const len = store.length;
    store = store.filter((r) => r.readerId !== id);
    return store.length < len;
  },
};
