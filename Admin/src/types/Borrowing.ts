export type BorrowingStatus =
  | "pending_payment"
  | "requested"
  | "borrowing"
  | "returned"
  | "overdue"
  | "cancelled";

export interface Borrowing {
  id: string;
  readerId: string;
  readerName: string;
  bookId: string;
  bookTitle: string;
  copyBarcode?: string;
  requestedDate?: string;
  borrowDate: string;
  dueDate: string;
  returnDate?: string;
  status: BorrowingStatus;
  borrowFee?: number;
  depositAmount?: number;
  totalAmount?: number;
  paidAmount?: number;
  paymentStatus?: string;
  paymentMethod?: string;
  depositRefunded?: boolean;
  remainingRefundAmount?: number;
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
  copyBarcode?: string;
  status: "requested";
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
