import type { SupportRequest } from "@/types/SupportRequest";

export const initialSupportRequests: SupportRequest[] = [
  {
    id: "SR-001",
    userName: "Nguyễn Văn A",
    userEmail: "nguyenvana@email.com",
    subject: "Không thể gửi yêu cầu mượn sách",
    message:
      "Tôi đã thử gửi yêu cầu mượn sách Clean Code nhiều lần nhưng app báo lỗi kết nối.",
    createdDate: "11/09/2026",
    status: "open",
  },
  {
    id: "SR-002",
    userName: "Trần Thị B",
    userEmail: "tranthib@email.com",
    subject: "Quên mật khẩu",
    message: "Tôi quên mật khẩu và không nhận được email reset.",
    createdDate: "10/09/2026",
    status: "in_progress",
    reply: "Chúng tôi đã gửi lại email reset mật khẩu.",
  },
  {
    id: "SR-003",
    userName: "Lê Văn C",
    userEmail: "levanc@email.com",
    subject: "Sách bị hư hỏng khi nhận",
    message: "Cuốn Kotlin in Action tôi nhận có trang bị rách.",
    createdDate: "08/09/2026",
    status: "resolved",
    reply: "Đã đổi bản sách mới cho bạn.",
  },
];
