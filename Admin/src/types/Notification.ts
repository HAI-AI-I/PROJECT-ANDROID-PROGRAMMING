export type NotificationType = "overdue" | "borrow_request" | "system";

export interface Notification {
  id: string;
  title: string;
  message: string;
  type: NotificationType;
  isRead: boolean;
  createdDate: string;
}

export interface NotificationFormData {
  title: string;
  message: string;
  type: NotificationType;
}
