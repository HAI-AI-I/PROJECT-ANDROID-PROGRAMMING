export type BookStatus = "available" | "borrowed" | "out_of_stock";

export interface Book {
  bookId: string;
  title: string;
  author: string;
  category: string;
  publisher: string;
  publishYear: number;
  quantity: number;
  availableQuantity: number;
  cover?: string;
}

export interface BookFormData {
  title: string;
  author: string;
  category: string;
  publisher: string;
  publishYear: string;
  quantity: string;
  cover?: string;
}

export interface BookBorrowHistory {
  id: string;
  readerName: string;
  borrowDate: string;
  dueDate: string;
  returnDate?: string;
  status: "borrowing" | "returned" | "overdue";
}

export function getBookStatus(book: Book): BookStatus {
  if (book.availableQuantity === 0) return "out_of_stock";
  if (book.availableQuantity < book.quantity) return "borrowed";
  return "available";
}
