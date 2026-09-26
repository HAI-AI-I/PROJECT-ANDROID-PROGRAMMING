export type BookStatus = "available" | "borrowed" | "out_of_stock";

export interface Book {
  bookId: string;
  isbn?: string;
  title: string;
  author: string;
  category: string;
  publisher?: string;
  publishYear?: number;
  quantity: number;
  availableQuantity: number;
  cover?: string;
  description?: string;
  borrowFee?: number;
  shelfLocation?: string;
  authorDetails?: BookAuthor[];
  publisherDetails?: BookPublisher;
}

export interface BookAuthor {
  id: number;
  name: string;
  penName?: string;
  birthDate?: string;
  deathDate?: string;
  nationality?: string;
}

export interface BookPublisher {
  id: number;
  name: string;
  address?: string;
  email?: string;
  phone?: string;
}

export interface BookFormData {
  isbn: string;
  title: string;
  author: string;
  category: string;
  publisher: string;
  publishYear: string;
  quantity: string;
  cover?: string;
  description: string;
  borrowFee: string;
  shelfLocation: string;
  authorDetails?: BookAuthor[];
  publisherDetails?: BookPublisher;
}

export interface BookBorrowHistory {
  id: string;
  readerId: string;
  readerName: string;
  copyBarcode: string;
  requestedDate: string;
  borrowDate: string;
  dueDate: string;
  returnDate?: string;
  status: "pending_payment" | "requested" | "borrowing" | "returned" | "overdue" | "cancelled";
}

export function getBookStatus(book: Book): BookStatus {
  if (book.availableQuantity === 0) return "out_of_stock";
  if (book.availableQuantity < book.quantity) return "borrowed";
  return "available";
}
