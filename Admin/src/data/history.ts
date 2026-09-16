import type { HistoryRecord } from "@/types/History";

export const initialHistory: HistoryRecord[] = [
  {
    id: "H-001",
    action: "borrow",
    userName: "Nguyễn Văn A",
    bookTitle: "Clean Code",
    description: "Mượn sách thành công",
    date: "01/09/2026",
  },
  {
    id: "H-002",
    action: "return",
    userName: "Trần Thị B",
    bookTitle: "Design Patterns",
    description: "Trả sách đúng hạn",
    date: "05/09/2026",
  },
  {
    id: "H-003",
    action: "extend",
    userName: "Lê Văn C",
    bookTitle: "Kotlin in Action",
    description: "Gia hạn thêm 7 ngày",
    date: "07/09/2026",
  },
  {
    id: "H-004",
    action: "fine",
    userName: "Nguyễn Văn A",
    bookTitle: "Clean Code",
    description: "Phạt quá hạn 15.000đ",
    date: "11/09/2026",
  },
  {
    id: "H-005",
    action: "cancel_request",
    userName: "Phạm Thị D",
    bookTitle: "Mạng máy tính căn bản",
    description: "Hủy yêu cầu mượn sách",
    date: "09/09/2026",
  },
  {
    id: "H-006",
    action: "borrow",
    userName: "Hoàng Văn E",
    bookTitle: "Code Dạo Ký Sự",
    description: "Mượn sách thành công",
    date: "10/09/2026",
  },
];
