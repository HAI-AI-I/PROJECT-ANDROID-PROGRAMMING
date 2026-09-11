import type { User } from "@/types/User";

export const initialUsers: User[] = [
  {
    userId: "U-001",
    name: "Admin",
    email: "admin@library.edu.vn",
    role: "admin",
    status: "active",
    createdDate: "01/01/2025",
  },
  {
    userId: "U-002",
    name: "Thủ thư Nguyễn",
    email: "librarian1@library.edu.vn",
    role: "librarian",
    status: "active",
    createdDate: "15/01/2025",
  },
  {
    userId: "U-003",
    name: "Thủ thư Trần",
    email: "librarian2@library.edu.vn",
    role: "librarian",
    status: "active",
    createdDate: "20/02/2025",
  },
  {
    userId: "U-004",
    name: "Nguyễn Văn A",
    email: "nguyenvana@email.com",
    role: "reader",
    status: "active",
    createdDate: "15/01/2025",
  },
  {
    userId: "U-005",
    name: "Trần Thị B",
    email: "tranthib@email.com",
    role: "reader",
    status: "active",
    createdDate: "20/02/2025",
  },
  {
    userId: "U-006",
    name: "Lê Văn C",
    email: "levanc@email.com",
    role: "reader",
    status: "inactive",
    createdDate: "10/03/2025",
  },
  ...Array.from({ length: 18 }, (_, index): User => {
    const number = index + 7;
    return {
      userId: `U-${String(number).padStart(3, "0")}`,
      name: ["Đặng Minh Huy", "Bùi Ngọc Lan", "Phan Tuấn Anh", "Đỗ Thanh Hà"][index % 4],
      email: `user${number}@library.edu.vn`,
      role: ["reader", "reader", "librarian"][index % 3] as User["role"],
      status: index % 5 === 0 ? "inactive" : "active",
      createdDate: `${String((index % 28) + 1).padStart(2, "0")}/${String((index % 9) + 1).padStart(2, "0")}/2026`,
    };
  }),
];
