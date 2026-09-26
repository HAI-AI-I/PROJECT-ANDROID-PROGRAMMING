import { apiClient, toApiId } from "@/services/apiClient";
import type { SupportRequest } from "@/types/SupportRequest";

interface ApiSupportRequest {
  id: number;
  userId: number;
  userFullName: string;
  userEmail: string;
  bookId?: number | null;
  bookTitle?: string | null;
  subject: string;
  message: string;
  status: string;
  adminReply?: string | null;
  repliedAt?: string | null;
  createdAt: string;
  updatedAt: string;
}

interface PagedResponse<T> {
  items: T[];
  total: number;
  page: number;
  pageSize: number;
  totalPages: number;
}

export const supportService = {
  async getRequests(): Promise<SupportRequest[]> {
    const result = await apiClient.get<PagedResponse<ApiSupportRequest>>(
      "/admin/support-requests?page=1&pageSize=100",
    );
    return result.items.map(mapRequest);
  },

  async getRequestById(id: string): Promise<SupportRequest> {
    const item = await apiClient.get<ApiSupportRequest>(
      `/admin/support-requests/${toApiId(id)}`,
    );
    return mapRequest(item);
  },

  async reply(id: string, reply: string): Promise<SupportRequest> {
    const item = await apiClient.patch<ApiSupportRequest>(
      `/admin/support-requests/${toApiId(id)}/reply`,
      { adminReply: reply.trim() },
    );
    return mapRequest(item);
  },

  async markResolved(id: string): Promise<SupportRequest> {
    const item = await apiClient.patch<ApiSupportRequest>(
      `/admin/support-requests/${toApiId(id)}/resolve`,
    );
    return mapRequest(item);
  },

  async closeRequest(id: string): Promise<SupportRequest> {
    const item = await apiClient.patch<ApiSupportRequest>(
      `/admin/support-requests/${toApiId(id)}/close`,
    );
    return mapRequest(item);
  },
};

function mapRequest(item: ApiSupportRequest): SupportRequest {
  return {
    id: `S-${String(item.id).padStart(3, "0")}`,
    userName: item.userFullName,
    userEmail: item.userEmail,
    bookTitle: item.bookTitle ?? undefined,
    subject: item.subject,
    message: item.message,
    createdDate: formatDateTime(item.createdAt),
    status: item.status.toLowerCase() as SupportRequest["status"],
    reply: item.adminReply ?? undefined,
    repliedDate: item.repliedAt ? formatDateTime(item.repliedAt) : undefined,
  };
}

function formatDateTime(value: string): string {
  return new Intl.DateTimeFormat("vi-VN", {
    dateStyle: "short",
    timeStyle: "short",
  }).format(new Date(value));
}
