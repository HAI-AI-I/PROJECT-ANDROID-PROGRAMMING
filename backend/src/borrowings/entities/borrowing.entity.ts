export class Borrowing {
  id: number;
  readerId: number;
  bookId: number;
  borrowDate: string;
  dueDate: string;
  returnDate?: string;
  status: 'PENDING' | 'BORROWING' | 'RETURNED' | 'OVERDUE' | 'REJECTED';
  note?: string;
}
