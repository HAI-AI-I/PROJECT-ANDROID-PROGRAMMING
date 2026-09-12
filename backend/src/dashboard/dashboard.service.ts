import { Injectable } from '@nestjs/common';

@Injectable()
export class DashboardService {
  getSummary() {
    return {
      totalBooks: 120,
      totalReaders: 48,
      borrowingBooks: 18,
      overdueBooks: 4,
      pendingRequests: 7,
      returnedBooks: 96,
    };
  }

  getRecentBorrowings() {
    return [
      { id: 1, readerName: 'Nguyễn Văn An', bookTitle: 'Clean Architecture', borrowDate: '2026-09-10', dueDate: '2026-09-17', status: 'BORROWING' },
      { id: 2, readerName: 'Trần Thị Bình', bookTitle: 'Design Patterns', borrowDate: '2026-09-08', dueDate: '2026-09-15', status: 'OVERDUE' },
      { id: 3, readerName: 'Lê Hoàng Cường', bookTitle: 'Kotlin in Action', borrowDate: '2026-09-11', dueDate: '2026-09-18', status: 'BORROWING' },
    ];
  }

  getOverdueBooks() {
    return [
      { id: 1, readerName: 'Trần Thị Bình', bookTitle: 'Design Patterns', overdueDays: 3 },
      { id: 2, readerName: 'Phạm Mỹ Duyên', bookTitle: 'Mạng máy tính căn bản', overdueDays: 2 },
    ];
  }

  getRecentActivities() {
    return [
      { id: 1, type: 'BORROW', description: 'Nguyễn Văn An mượn Clean Architecture', createdAt: '2026-09-12T08:30:00Z' },
      { id: 2, type: 'RETURN', description: 'Mai Thu Thảo trả sách JavaScript Nâng Cao', createdAt: '2026-09-12T07:15:00Z' },
      { id: 3, type: 'FINE', description: 'Lê Hoàng Cường bị phạt 20.000đ', createdAt: '2026-09-11T17:00:00Z' },
    ];
  }
}
