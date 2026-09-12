import { apiClient, toApiId } from "@/services/apiClient";
import type { Reader, ReaderFormData } from "@/types/Reader";


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

export const readerService = {
  async getReaders(filters: ReaderFilters = {}): Promise<PaginatedResult<Reader>> {
    const page = filters.page ?? 1;
    const pageSize = filters.pageSize ?? 8;
    const params = new URLSearchParams();
    if (filters.search) params.set("search", filters.search);
    const readers = await apiClient.get<ApiReader[]>(`/readers?${params.toString()}`);
    const filtered = readers.map(mapReader).filter((reader) => !filters.status || reader.status === filters.status);
    const total = filtered.length;
    const totalPages = Math.max(1, Math.ceil(total / pageSize));
    const start = (page - 1) * pageSize;
    return { items: filtered.slice(start, start + pageSize), total, page, pageSize, totalPages };
  },

  async getReaderById(id: string): Promise<Reader | null> {
    try { return mapReader(await apiClient.get<ApiReader>(`/readers/${toApiId(id)}`)); } catch { return null; }
  },

  async createReader(data: ReaderFormData): Promise<Reader> {
    return mapReader(await apiClient.post<ApiReader>("/readers", { userId: 1, fullName: data.name.trim(), email: data.email.trim(), phone: data.phone.trim(), status: data.status }));
  },

  async updateReader(id: string, data: ReaderFormData): Promise<Reader | null> {
    try { return mapReader(await apiClient.patch<ApiReader>(`/readers/${toApiId(id)}`, { fullName: data.name.trim(), email: data.email.trim(), phone: data.phone.trim(), status: data.status })); } catch { return null; }
  },

  async deleteReader(id: string): Promise<boolean> {
    try { await apiClient.delete(`/readers/${toApiId(id)}`); return true; } catch { return false; }
  },
};

interface ApiReader { id: number; fullName: string; email: string; phone: string; avatar?: string; totalBorrowing: number; status: string; createdAt: string; }
function mapReader(reader: ApiReader): Reader { return { readerId: `R-${String(reader.id).padStart(3, "0")}`, name: reader.fullName, email: reader.email, phone: reader.phone, avatar: reader.avatar, booksBorrowing: reader.totalBorrowing, status: (reader.status === "blocked" ? "suspended" : reader.status) as Reader["status"], registeredDate: new Date(reader.createdAt).toLocaleDateString("vi-VN") }; }
