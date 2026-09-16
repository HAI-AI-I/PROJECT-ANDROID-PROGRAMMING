import { apiClient, toApiId } from "@/services/apiClient";
import type { Notification, NotificationFormData } from "@/types/Notification";

const notifyChanged = () => {
  if (typeof window !== "undefined") {
    window.dispatchEvent(new Event("notifications:changed"));
  }
};

export const notificationService = {
  async getNotifications(): Promise<Notification[]> {
    const items = await apiClient.get<ApiNotification[]>("/notifications");
    return items.map(mapNotification);
  },

  async markAsRead(id: string): Promise<void> {
    await apiClient.patch(`/notifications/${toApiId(id)}/read`);
    notifyChanged();
  },

  async markAllAsRead(): Promise<void> {
    await apiClient.patch("/notifications/read-all");
    notifyChanged();
  },

  async deleteNotification(id: string): Promise<boolean> {
    await apiClient.delete(`/notifications/${toApiId(id)}`);
    notifyChanged();
    return true;
  },

  async createNotification(data: NotificationFormData): Promise<Notification> {
    const notification = await apiClient.post<ApiNotification>("/notifications", {
      userId: 1,
      title: data.title.trim(),
      message: data.message.trim(),
      type: data.type === "overdue" ? "OVERDUE" : data.type === "borrow_request" ? "BORROW" : "SYSTEM",
    });
    notifyChanged();
    return mapNotification(notification);
  },
};

interface ApiNotification { id: number; title: string; message: string; type: string; isRead: boolean; createdAt: string; }
function mapNotification(item: ApiNotification): Notification {
  return {
    id: `N-${String(item.id).padStart(3, "0")}`,
    title: item.title,
    message: item.message,
    type: item.type === "OVERDUE" ? "overdue" : item.type === "BORROW" ? "borrow_request" : "system",
    isRead: item.isRead,
    createdDate: new Date(item.createdAt).toLocaleDateString("vi-VN"),
  };
}
