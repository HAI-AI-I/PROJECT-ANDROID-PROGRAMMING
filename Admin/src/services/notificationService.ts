import { initialNotifications } from "@/data/notifications";
import type { Notification, NotificationFormData } from "@/types/Notification";

let store: Notification[] = [...initialNotifications];
const delay = (ms = 300) => new Promise((r) => setTimeout(r, ms));
const notifyChanged = () => {
  if (typeof window !== "undefined") {
    window.dispatchEvent(new Event("notifications:changed"));
  }
};

export const notificationService = {
  async getNotifications(): Promise<Notification[]> {
    await delay();
    return [...store];
  },

  async markAsRead(id: string): Promise<void> {
    await delay();
    store = store.map((n) => (n.id === id ? { ...n, isRead: true } : n));
    notifyChanged();
  },

  async markAllAsRead(): Promise<void> {
    await delay();
    store = store.map((n) => ({ ...n, isRead: true }));
    notifyChanged();
  },

  async deleteNotification(id: string): Promise<boolean> {
    await delay();
    const len = store.length;
    store = store.filter((n) => n.id !== id);
    notifyChanged();
    return store.length < len;
  },

  async createNotification(data: NotificationFormData): Promise<Notification> {
    await delay();
    const notification: Notification = {
      id: `N-${String(store.length + 1).padStart(3, "0")}`,
      title: data.title.trim(),
      message: data.message.trim(),
      type: data.type,
      isRead: false,
      createdDate: new Date().toLocaleDateString("vi-VN"),
    };
    store = [notification, ...store];
    notifyChanged();
    return notification;
  },
};
