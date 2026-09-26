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
    if (filters.role) params.set("role", filters.role === "reader" ? "USER" : "ADMIN");
    if (filters.status) params.set("active", String(filters.status === "active"));
    params.set("page", String(page));
    params.set("pageSize", String(pageSize));
    const result = await apiClient.get<PaginatedResult<ApiUser>>(`/admin/users?${params.toString()}`);
    return {
      ...result,
      items: result.items.map((user) => ({
        userId: `U-${String(user.id).padStart(3, "0")}`,
        name: user.fullName,
        email: user.email,
        role: (user.role === "ADMIN" ? "admin" : "reader") as User["role"],
        status: (user.active ? "active" : "inactive") as User["status"],
        avatar: undefined,
        createdDate: new Date(user.createdAt).toLocaleDateString("vi-VN"),
      })),
    };
  },
};

interface ApiUser { id: number; fullName: string; email: string; phone?: string; role: string; active: boolean; createdAt: string; }
