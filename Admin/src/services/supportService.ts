import { apiClient, toApiId } from "@/services/apiClient";
import type { SupportRequest } from "@/types/SupportRequest";

export const supportService = {
  async getRequests(): Promise<SupportRequest[]> {
    const items = await apiClient.get<ApiSupportRequest[]>("/support");
    return items.map(mapRequest);
  },

  async getRequestById(id: string): Promise<SupportRequest | null> {
    try { return mapRequest(await apiClient.get<ApiSupportRequest>(`/support/${toApiId(id)}`)); } catch { return null; }
  },

  async reply(id: string, reply: string): Promise<SupportRequest | null> {
    try { return mapRequest(await apiClient.patch<ApiSupportRequest>(`/support/${toApiId(id)}/reply`, { adminReply: reply })); } catch { return null; }
  },

  async markResolved(id: string): Promise<SupportRequest | null> {
    try { return mapRequest(await apiClient.patch<ApiSupportRequest>(`/support/${toApiId(id)}/resolve`)); } catch { return null; }
  },

  async closeRequest(id: string): Promise<SupportRequest | null> {
    try { return mapRequest(await apiClient.patch<ApiSupportRequest>(`/support/${toApiId(id)}/close`)); } catch { return null; }
  },
};

interface ApiSupportRequest { id: number; userId: number; subject: string; message: string; status: string; adminReply?: string; createdAt: string; }
function mapRequest(item: ApiSupportRequest): SupportRequest { return { id: `S-${String(item.id).padStart(3, "0")}`, userName: `User #${item.userId}`, userEmail: "", subject: item.subject, message: item.message, createdDate: new Date(item.createdAt).toLocaleDateString("vi-VN"), status: item.status.toLowerCase() as SupportRequest["status"], reply: item.adminReply }; }
