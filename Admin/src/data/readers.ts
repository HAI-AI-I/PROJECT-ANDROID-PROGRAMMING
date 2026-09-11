import type { Reader } from "@/types/Reader";

export const initialReaders: Reader[] = [
  {
    readerId: "R-001",
    name: "Nguyễn Văn A",
    email: "nguyenvana@email.com",
    phone: "0901234567",
    booksBorrowing: 2,
    status: "active",
    registeredDate: "15/01/2025",
  },
  {
    readerId: "R-002",
    name: "Trần Thị B",
    email: "tranthib@email.com",
    phone: "0912345678",
    booksBorrowing: 1,
    status: "active",
    registeredDate: "20/02/2025",
  },
  {
    readerId: "R-003",
    name: "Lê Văn C",
    email: "levanc@email.com",
    phone: "0923456789",
    booksBorrowing: 0,
    status: "active",
    registeredDate: "10/03/2025",
  },
  {
    readerId: "R-004",
    name: "Phạm Thị D",
    email: "phamthid@email.com",
    phone: "0934567890",
    booksBorrowing: 3,
    status: "active",
    registeredDate: "05/04/2025",
  },
  {
    readerId: "R-005",
    name: "Hoàng Văn E",
    email: "hoangvane@email.com",
    phone: "0945678901",
    booksBorrowing: 0,
    status: "inactive",
    registeredDate: "12/05/2025",
  },
  {
    readerId: "R-006",
    name: "Võ Thị F",
    email: "vothif@email.com",
    phone: "0956789012",
    booksBorrowing: 1,
    status: "suspended",
    registeredDate: "18/06/2025",
  },
  ...Array.from({ length: 18 }, (_, index): Reader => {
    const number = index + 7;
    return {
      readerId: `R-${String(number).padStart(3, "0")}`,
      name: ["Đặng Minh Huy", "Bùi Ngọc Lan", "Phan Tuấn Anh", "Đỗ Thanh Hà"][index % 4],
      email: `reader${number}@email.com`,
      phone: `09${String(70000000 + number).slice(-8)}`,
      booksBorrowing: index % 4,
      status: ["active", "active", "inactive", "suspended"][index % 4] as Reader["status"],
      registeredDate: `${String((index % 28) + 1).padStart(2, "0")}/${String((index % 9) + 1).padStart(2, "0")}/2026`,
    };
  }),
];
