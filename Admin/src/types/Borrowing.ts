export type BorrowingStatus =
  | "borrowing"
  | "returned"
  | "overdue"
  | "pending";

export interface Borrowing {
  id: string;
  readerId: string;
  readerName: string;
  bookId: string;
  bookTitle: string;
  borrowDate: string;
  dueDate: string;
  returnDate?: string;
  status: BorrowingStatus;
  overdueDays?: number;
  fineAmount?: number;
}

export interface BorrowRequest {
  id: string;
  readerId: string;
  readerName: string;
  bookId: string;
  bookTitle: string;
  requestDate: string;
  status: "pending" | "approved" | "rejected";
}

export interface OverdueBook {
  id: string;
  bookTitle: string;
  readerName: string;
  dueDate: string;
  overdueDays: number;
}

export interface BorrowingChartData {
  day: string;
  count: number;
}

export interface DashboardStats {
  totalBooks: number;
  currentlyBorrowed: number;
  totalReaders: number;
  overdueCount: number;
}
