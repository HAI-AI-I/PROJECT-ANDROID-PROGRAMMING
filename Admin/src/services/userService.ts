import { apiClient } from "@/services/apiClient";
import type { User } from "@/types/User";


export interface UserFilters {
  search?: string;
  role?: string;
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

export const userService = {
  async getUsers(filters: UserFilters = {}): Promise<PaginatedResult<User>> {
    const page = filters.page ?? 1;
    const pageSize = filters.pageSize ?? 8;
    const params = new URLSearchParams();
    if (filters.search) params.set("search", filters.search);
    const users = await apiClient.get<ApiUser[]>(`/admin/users?${params.toString()}`);
    let filtered = users.map((user) => ({ userId: `U-${String(user.id).padStart(3, "0")}`, name: user.fullName, email: user.email, role: user.role.toLowerCase() as User["role"], status: (user.active ? "active" : "inactive") as User["status"], avatar: undefined, createdDate: new Date(user.createdAt).toLocaleDateString("vi-VN") }));
    if (filters.search) {
      const query = filters.search.toLowerCase();
      filtered = filtered.filter((user) => user.name.toLowerCase().includes(query) || user.email.toLowerCase().includes(query));
    }
    if (filters.role) filtered = filtered.filter((user) => user.role === filters.role);
    if (filters.status) filtered = filtered.filter((user) => user.status === filters.status);
    const total = filtered.length;
    const totalPages = Math.max(1, Math.ceil(total / pageSize));
    const start = (page - 1) * pageSize;
    return { items: filtered.slice(start, start + pageSize), total, page, pageSize, totalPages };
  },
};

interface ApiUser { id: number; fullName: string; email: string; phone?: string; role: string; active: boolean; createdAt: string; }
