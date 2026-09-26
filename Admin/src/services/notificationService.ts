import { apiClient, toApiId } from "@/services/apiClient";
import type { Notification, NotificationFormData, NotificationType } from "@/types/Notification";

const notifyChanged = () => {
  if (typeof window !== "undefined") window.dispatchEvent(new Event("notifications:changed"));
};

export const notificationService = {
  async getNotifications(): Promise<Notification[]> {
    const items = await apiClient.get<ApiNotification[]>("/notifications?page=0&limit=100");
    return items.map(mapNotification);
  },

  async getUnreadCount(): Promise<number> {
    const response = await apiClient.get<{ unreadCount: number }>("/notifications/unread-count");
    return response.unreadCount;
  },

  async markAsRead(id: string): Promise<void> {
    await apiClient.patch(`/notifications/${toApiId(id)}/read`);
    notifyChanged();
  },

  async markAllAsRead(): Promise<void> {
    await apiClient.patch("/notifications/read-all");
    notifyChanged();
  },

  async deleteNotification(id: string): Promise<void> {
    await apiClient.delete(`/notifications/${toApiId(id)}`);
    notifyChanged();
  },

  async broadcast(data: NotificationFormData): Promise<number> {
    const response = await apiClient.post<{ recipientCount: number }>(
      "/admin/notifications/broadcast",
      {
        title: data.title.trim(),
        message: data.message.trim(),
        type: data.type.toUpperCase(),
      }
    );
    return response.recipientCount;
  },
};

interface ApiNotification {
  id: number;
  title: string;
  message: string;
  type: string;
  isRead: boolean;
  createdAt: string;
}

function mapNotification(item: ApiNotification): Notification {
  const typeMap: Record<string, NotificationType> = {
    INFO: "info",
    WARNING: "warning",
    SUCCESS: "success",
    ERROR: "error",
    BOOK: "info",
  };
  return {
    id: `N-${String(item.id).padStart(3, "0")}`,
    title: item.title,
    message: item.message,
    type: typeMap[item.type] ?? "info",
    isRead: item.isRead,
    createdDate: new Date(item.createdAt).toLocaleString("vi-VN"),
  };
}
