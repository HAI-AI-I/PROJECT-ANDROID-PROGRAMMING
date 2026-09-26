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
    const params = new URLSearchParams({
      page: String(filters.page ?? 1),
      pageSize: String(filters.pageSize ?? 8),
    });
    if (filters.search?.trim()) params.set("search", filters.search.trim());
    if (filters.status) params.set("active", String(filters.status === "active"));

    const result = await apiClient.get<ApiPagedReaders>(`/admin/readers?${params}`);
    return { ...result, items: result.items.map(mapReader) };
  },

  async getReaderById(id: string): Promise<Reader> {
    return mapReader(await apiClient.get<ApiReader>(`/admin/readers/${toApiId(id)}`));
  },

  async createReader(data: ReaderFormData): Promise<Reader> {
    return mapReader(await apiClient.post<ApiReader>("/admin/readers", {
      fullName: data.name.trim(),
      email: data.email.trim(),
      phone: data.phone.trim(),
      password: data.password,
      active: data.status === "active",
    }));
  },

  async updateReader(id: string, data: ReaderFormData): Promise<Reader> {
    return mapReader(await apiClient.patch<ApiReader>(`/admin/readers/${toApiId(id)}`, {
      fullName: data.name.trim(),
      email: data.email.trim(),
      phone: data.phone.trim(),
      active: data.status === "active",
    }));
  },

  async deactivateReader(id: string): Promise<void> {
    await apiClient.delete<void>(`/admin/readers/${toApiId(id)}`);
  },
};

interface ApiPagedReaders {
  items: ApiReader[];
  total: number;
  page: number;
  pageSize: number;
  totalPages: number;
}

interface ApiReader {
  id: number;
  fullName: string;
  email: string;
  phone: string;
  active: boolean;
  activeBorrowingCount: number;
  createdAt: string;
}

function mapReader(reader: ApiReader): Reader {
  return {
    readerId: `R-${String(reader.id).padStart(3, "0")}`,
    name: reader.fullName,
    email: reader.email,
    phone: reader.phone,
    booksBorrowing: reader.activeBorrowingCount,
    status: reader.active ? "active" : "inactive",
    registeredDate: new Date(reader.createdAt).toLocaleDateString("vi-VN"),
  };
}
