import { initialUsers } from "@/data/users";
import type { User } from "@/types/User";

let store: User[] = [...initialUsers];
const delay = (ms = 300) => new Promise((r) => setTimeout(r, ms));

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

function filter(filters: UserFilters): User[] {
  let result = [...store];
  if (filters.search) {
    const q = filters.search.toLowerCase();
    result = result.filter(
      (u) =>
        u.name.toLowerCase().includes(q) ||
        u.email.toLowerCase().includes(q) ||
        u.userId.toLowerCase().includes(q)
    );
  }
  if (filters.role) result = result.filter((u) => u.role === filters.role);
  if (filters.status) result = result.filter((u) => u.status === filters.status);
  return result;
}

export const userService = {
  async getUsers(filters: UserFilters = {}): Promise<PaginatedResult<User>> {
    await delay();
    const page = filters.page ?? 1;
    const pageSize = filters.pageSize ?? 8;
    const filtered = filter(filters);
    const total = filtered.length;
    const totalPages = Math.max(1, Math.ceil(total / pageSize));
    const start = (page - 1) * pageSize;
    return { items: filtered.slice(start, start + pageSize), total, page, pageSize, totalPages };
  },
};
