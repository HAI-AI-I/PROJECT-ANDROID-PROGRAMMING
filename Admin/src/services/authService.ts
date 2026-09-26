import { apiClient, clearAuthSession } from "@/services/apiClient";

const ACCESS_TOKEN_KEY = "library_access_token";
const USER_KEY = "library_user";

export interface AuthUser {
  id: number;
  fullName: string;
  email: string;
  phone?: string;
  role: string;
  active: boolean;
}

interface AuthResponse {
  accessToken: string;
  tokenType: string;
  user: AuthUser;
}

function saveSession(response: AuthResponse) {
  window.localStorage.setItem(ACCESS_TOKEN_KEY, response.accessToken);
  window.localStorage.setItem(USER_KEY, JSON.stringify(response.user));
}

function readStoredUser(): AuthUser | null {
  if (typeof window === "undefined") return null;
  const rawUser = window.localStorage.getItem(USER_KEY);
  if (!rawUser) return null;
  try {
    return JSON.parse(rawUser) as AuthUser;
  } catch {
    clearAuthSession();
    return null;
  }
}

export const authService = {
  async login(identifier: string, password: string): Promise<AuthUser> {
    const response = await apiClient.post<AuthResponse>("/auth/login", {
      identifier: identifier.trim(),
      password,
    });

    saveSession(response);
    if (response.user.role.toUpperCase() !== "ADMIN") {
      try {
        await apiClient.post<void>("/auth/logout", {});
      } finally {
        clearAuthSession();
      }
      throw new Error("Tài khoản này không có quyền truy cập trang quản trị.");
    }

    return response.user;
  },

  hasAdminSession(): boolean {
    if (typeof window === "undefined") return false;
    const token = window.localStorage.getItem(ACCESS_TOKEN_KEY);
    const user = readStoredUser();
    return Boolean(token && user?.active && user.role.toUpperCase() === "ADMIN");
  },

  async getCurrentUser(): Promise<AuthUser> {
    const user = await apiClient.get<AuthUser>("/auth/me");
    if (!user.active || user.role.toUpperCase() !== "ADMIN") {
      clearAuthSession();
      throw new Error("Tài khoản không có quyền quản trị hoặc đã bị vô hiệu hóa.");
    }
    if (typeof window !== "undefined") {
      window.localStorage.setItem(USER_KEY, JSON.stringify(user));
    }
    return user;
  },

  async validateAdminSession(): Promise<boolean> {
    if (!this.hasAdminSession()) return false;
    try {
      await this.getCurrentUser();
      return true;
    } catch {
      return false;
    }
  },

  async logout(): Promise<void> {
    try {
      if (typeof window !== "undefined" && window.localStorage.getItem(ACCESS_TOKEN_KEY)) {
        await apiClient.post<void>("/auth/logout", {});
      }
    } catch {
      // Phiên cục bộ vẫn phải được xóa khi máy chủ không phản hồi.
    } finally {
      clearAuthSession();
    }
  },
};
