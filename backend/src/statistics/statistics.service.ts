import { Injectable } from '@nestjs/common';

@Injectable()
export class StatisticsService {
  getSummary() {
    return {
      totalBorrowings: 258,
      totalReturns: 214,
      totalOverdue: 18,
      totalBooks: 120,
      totalReaders: 48,
    };
  }

  getBorrowingTrend() {
    return [
      { month: 'January', count: 110 },
      { month: 'February', count: 120 },
      { month: 'March', count: 130 },
      { month: 'April', count: 145 },
      { month: 'May', count: 155 },
      { month: 'June', count: 170 },
    ];
  }

  getBooksByCategory() {
    return [
      { name: 'Lập trình', value: 42 },
      { name: 'Cơ sở dữ liệu', value: 18 },
      { name: 'Mạng máy tính', value: 15 },
      { name: 'Khoa học', value: 10 },
      { name: 'Khác', value: 8 },
    ];
  }

  getPopularBooks() {
    return [
      { title: 'Clean Architecture', count: 86 },
      { title: 'Design Patterns', count: 74 },
      { title: 'Kotlin in Action', count: 63 },
      { title: 'Clean Code', count: 58 },
    ];
  }

  getActiveReaders() {
    return [
      { name: 'Nguyễn Văn An', value: 12 },
      { name: 'Trần Thị Bình', value: 10 },
      { name: 'Mai Thu Thảo', value: 9 },
      { name: 'Lê Hoàng Cường', value: 8 },
    ];
  }
}
