import type {
  Borrowing,
  BorrowRequest,
  BorrowingChartData,
  DashboardStats,
  OverdueBook,
} from "@/types/Borrowing";

export const dashboardStats: DashboardStats = {
  totalBooks: 1250,
  currentlyBorrowed: 326,
  totalReaders: 856,
  overdueCount: 18,
};

export const borrowingChartData: BorrowingChartData[] = [
  { day: "T2", count: 42 },
  { day: "T3", count: 58 },
  { day: "T4", count: 45 },
  { day: "T5", count: 72 },
  { day: "T6", count: 65 },
  { day: "T7", count: 38 },
  { day: "CN", count: 28 },
];

export const recentBorrowings: Borrowing[] = [
  {
    id: "BR-001",
    readerId: "R-001",
    readerName: "Nguyễn Văn A",
    bookId: "B-001",
    bookTitle: "Clean Code",
    borrowDate: "01/09/2026",
    dueDate: "08/09/2026",
    status: "overdue",
    overdueDays: 3,
    fineAmount: 15000,
  },
  {
    id: "BR-002",
    readerId: "R-002",
    readerName: "Trần Thị B",
    bookId: "B-002",
    bookTitle: "Design Patterns",
    borrowDate: "05/09/2026",
    dueDate: "19/09/2026",
    status: "borrowing",
  },
  {
    id: "BR-003",
    readerId: "R-003",
    readerName: "Lê Văn C",
    bookId: "B-003",
    bookTitle: "Clean Architecture",
    borrowDate: "08/09/2026",
    dueDate: "22/09/2026",
    status: "borrowing",
  },
  {
    id: "BR-004",
    readerId: "R-004",
    readerName: "Phạm Thị D",
    bookId: "B-004",
    bookTitle: "Mạng máy tính căn bản",
    borrowDate: "10/09/2026",
    dueDate: "24/09/2026",
    status: "pending",
  },
  {
    id: "BR-005",
    readerId: "R-005",
    readerName: "Hoàng Văn E",
    bookId: "B-005",
    bookTitle: "Code Dạo Ký Sự",
    borrowDate: "20/07/2026",
    dueDate: "03/08/2026",
    returnDate: "02/08/2026",
    status: "returned",
  },
  ...Array.from({ length: 19 }, (_, index): Borrowing => {
    const number = index + 6;
    const status = ["borrowing", "returned", "overdue"][index % 3] as Borrowing["status"];
    return {
      id: `BR-${String(number).padStart(3, "0")}`,
      readerId: `R-${String((index % 12) + 1).padStart(3, "0")}`,
      readerName: ["Đặng Minh Huy", "Bùi Ngọc Lan", "Phan Tuấn Anh", "Đỗ Thanh Hà"][index % 4],
      bookId: `B-${String((index % 24) + 1).padStart(3, "0")}`,
      bookTitle: ["Clean Code", "Design Patterns", "Kotlin in Action", "Kiến trúc phần mềm"][index % 4],
      borrowDate: `${String((index % 20) + 1).padStart(2, "0")}/09/2026`,
      dueDate: `${String((index % 20) + 10).padStart(2, "0")}/09/2026`,
      status,
      ...(status === "overdue" ? { overdueDays: (index % 6) + 1, fineAmount: ((index % 6) + 1) * 5000 } : {}),
      ...(status === "returned" ? { returnDate: `${String((index % 20) + 8).padStart(2, "0")}/09/2026` } : {}),
    };
  }),
];

export const overdueBooks: OverdueBook[] = [
  {
    id: "OB-001",
    bookTitle: "Clean Code",
    readerName: "Nguyễn Văn A",
    dueDate: "08/09/2026",
    overdueDays: 3,
  },
  {
    id: "OB-002",
    bookTitle: "Clean Architecture",
    readerName: "Võ Thị F",
    dueDate: "05/09/2026",
    overdueDays: 6,
  },
  {
    id: "OB-003",
    bookTitle: "Design Patterns",
    readerName: "Đặng Văn G",
    dueDate: "07/09/2026",
    overdueDays: 4,
  },
];

export const initialBorrowRequests: BorrowRequest[] = [
  {
    id: "REQ-001",
    readerId: "R-004",
    readerName: "Phạm Thị D",
    bookId: "B-002",
    bookTitle: "Design Patterns",
    requestDate: "11/09/2026",
    status: "pending",
  },
  {
    id: "REQ-002",
    readerId: "R-001",
    readerName: "Nguyễn Văn A",
    bookId: "B-008",
    bookTitle: "Code Dạo Ký Sự",
    requestDate: "11/09/2026",
    status: "pending",
  },
  {
    id: "REQ-003",
    readerId: "R-003",
    readerName: "Lê Văn C",
    bookId: "B-003",
    bookTitle: "Kotlin in Action",
    requestDate: "10/09/2026",
    status: "pending",
  },
];
