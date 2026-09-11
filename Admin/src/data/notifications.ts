import type { Notification } from "@/types/Notification";

export const initialNotifications: Notification[] = [
  {
    id: "N-001",
    title: "Sách quá hạn",
    message: "Nguyễn Văn A chưa trả sách Clean Code (quá hạn 3 ngày).",
    type: "overdue",
    isRead: false,
    createdDate: "11/09/2026",
  },
  {
    id: "N-002",
    title: "Yêu cầu mượn mới",
    message: "Phạm Thị D yêu cầu mượn sách Design Patterns.",
    type: "borrow_request",
    isRead: false,
    createdDate: "11/09/2026",
  },
  {
    id: "N-003",
    title: "Bảo trì hệ thống",
    message: "Hệ thống sẽ bảo trì vào 00:00 ngày 15/09/2026.",
    type: "system",
    isRead: true,
    createdDate: "10/09/2026",
  },
  {
    id: "N-004",
    title: "Sách quá hạn",
    message: "Võ Thị F chưa trả sách Clean Architecture (quá hạn 6 ngày).",
    type: "overdue",
    isRead: true,
    createdDate: "09/09/2026",
  },
];
