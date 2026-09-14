import type { Book, BookBorrowHistory } from "@/types/Book";

export const initialBooks: Book[] = [
  {
    bookId: "B-001",
    title: "Clean Architecture",
    author: "Robert C. Martin",
    category: "Lập trình",
    publisher: "Prentice Hall",
    publishYear: 2017,
    quantity: 10,
    availableQuantity: 2,
  },
  {
    bookId: "B-002",
    title: "Design Patterns",
    author: "Gang of Four",
    category: "Lập trình",
    publisher: "Addison-Wesley",
    publishYear: 1994,
    quantity: 8,
    availableQuantity: 5,
  },
  {
    bookId: "B-003",
    title: "Kotlin in Action",
    author: "Dmitry Jemerov",
    category: "Lập trình",
    publisher: "Manning",
    publishYear: 2017,
    quantity: 6,
    availableQuantity: 1,
  },
  {
    bookId: "B-004",
    title: "Cấu trúc dữ liệu và giải thuật nâng cao",
    author: "Nguyễn Văn A",
    category: "Lập trình",
    publisher: "NXB Giáo dục Việt Nam",
    publishYear: 2020,
    quantity: 12,
    availableQuantity: 3,
  },
  {
    bookId: "B-005",
    title: "Hệ quản trị cơ sở dữ liệu quan hệ",
    author: "Trần Thị B",
    category: "Cơ sở dữ liệu",
    publisher: "NXB Đại học Quốc gia",
    publishYear: 2019,
    quantity: 5,
    availableQuantity: 0,
  },
  {
    bookId: "B-006",
    title: "Mạng máy tính căn bản",
    author: "Lê Văn C",
    category: "Mạng máy tính",
    publisher: "NXB Thống kê",
    publishYear: 2021,
    quantity: 15,
    availableQuantity: 5,
  },
  {
    bookId: "B-007",
    title: "Clean Code",
    author: "Robert C. Martin",
    category: "Lập trình",
    publisher: "Prentice Hall",
    publishYear: 2008,
    quantity: 7,
    availableQuantity: 0,
  },
  {
    bookId: "B-008",
    title: "Code Dạo Ký Sự",
    author: "Phạm Huy Hoàng",
    category: "Lập trình",
    publisher: "Self-published",
    publishYear: 2022,
    quantity: 4,
    availableQuantity: 2,
  },
  ...Array.from({ length: 16 }, (_, index): Book => {
    const number = index + 9;
    const titles = [
      "Lập trình hướng đối tượng",
      "JavaScript hiện đại",
      "Thiết kế cơ sở dữ liệu",
      "Nhập môn trí tuệ nhân tạo",
      "Mạng máy tính nâng cao",
      "Phát triển ứng dụng Android",
      "Thuật toán và cấu trúc dữ liệu",
      "Kiến trúc phần mềm",
    ];
    const title = titles[index % titles.length];
    const quantity = 5 + (index % 5);
    const availableQuantity = index % 4 === 0 ? 0 : 1 + (index % quantity);

    return {
      bookId: `B-${String(number).padStart(3, "0")}`,
      title: `${title} ${Math.floor(index / titles.length) + 1}`,
      author: ["Nguyễn Minh Anh", "Trần Quốc Bảo", "Lê Hoàng Nam", "Phạm Thu Hà"][index % 4],
      category: ["Lập trình", "Cơ sở dữ liệu", "Mạng máy tính", "Khoa học"][index % 4],
      publisher: "NXB Thông tin và Truyền thông",
      publishYear: 2020 + (index % 5),
      quantity,
      availableQuantity,
    };
  }),
];

export const bookCategories = [
  "Lập trình",
  "Cơ sở dữ liệu",
  "Mạng máy tính",
  "Khoa học",
  "Văn học",
];

export const bookBorrowHistory: Record<string, BookBorrowHistory[]> = {
  "B-001": [
    {
      id: "BH-001",
      readerName: "Nguyễn Văn A",
      borrowDate: "01/09/2026",
      dueDate: "15/09/2026",
      status: "borrowing",
    },
    {
      id: "BH-002",
      readerName: "Trần Thị B",
      borrowDate: "20/08/2026",
      dueDate: "03/09/2026",
      returnDate: "02/09/2026",
      status: "returned",
    },
  ],
  "B-007": [
    {
      id: "BH-003",
      readerName: "Nguyễn Văn A",
      borrowDate: "01/09/2026",
      dueDate: "08/09/2026",
      status: "overdue",
    },
  ],
};
