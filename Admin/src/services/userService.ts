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
    if (filters.role) params.set("role", filters.role.toUpperCase());
    if (filters.status) params.set("status", filters.status.toUpperCase());
    const users = await apiClient.get<ApiUser[]>(`/users?${params.toString()}`);
    const filtered = users.map((user) => ({ userId: `U-${String(user.id).padStart(3, "0")}`, name: user.fullName, email: user.email, role: user.role.toLowerCase() as User["role"], status: (user.status.toLowerCase() === "blocked" ? "inactive" : user.status.toLowerCase()) as User["status"], avatar: user.avatar, createdDate: new Date(user.createdAt).toLocaleDateString("vi-VN") }));
    const total = filtered.length;
    const totalPages = Math.max(1, Math.ceil(total / pageSize));
    const start = (page - 1) * pageSize;
    return { items: filtered.slice(start, start + pageSize), total, page, pageSize, totalPages };
  },
};

interface ApiUser { id: number; fullName: string; email: string; role: string; status: string; avatar?: string; createdAt: string; }
