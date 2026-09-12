import { Injectable, NotFoundException } from '@nestjs/common';
import { CreateBorrowingDto } from './dto/create-borrowing.dto.js';
import { UpdateBorrowingDto } from './dto/update-borrowing.dto.js';
import { Borrowing } from './entities/borrowing.entity.js';

@Injectable()
export class BorrowingsService {
  private readonly borrowings: Borrowing[] = [
    { id: 1, readerId: 1, bookId: 1, borrowDate: '2026-09-10', dueDate: '2026-09-17', status: 'BORROWING', note: 'Đọc trong 7 ngày' },
    { id: 2, readerId: 2, bookId: 2, borrowDate: '2026-09-01', dueDate: '2026-09-08', status: 'OVERDUE', note: 'Quá hạn 2 ngày' },
    { id: 3, readerId: 3, bookId: 5, borrowDate: '2026-09-11', dueDate: '2026-09-18', status: 'PENDING', note: 'Chờ duyệt' },
    { id: 4, readerId: 4, bookId: 4, borrowDate: '2026-09-05', dueDate: '2026-09-12', status: 'RETURNED', returnDate: '2026-09-12', note: 'Đã trả' },
  ];

  private nextId = this.borrowings.length + 1;

  findAll() {
    return [...this.borrowings];
  }

  findOne(id: number) {
    const borrowing = this.borrowings.find((item) => item.id === id);
    if (!borrowing) throw new NotFoundException('Borrowing not found');
    return borrowing;
  }

  create(dto: CreateBorrowingDto) {
    const borrowing: Borrowing = {
      id: this.nextId++,
      readerId: dto.readerId,
      bookId: dto.bookId,
      borrowDate: dto.borrowDate,
      dueDate: dto.dueDate,
      status: 'PENDING',
      note: dto.note,
    };
    this.borrowings.unshift(borrowing);
    return borrowing;
  }

  update(id: number, dto: UpdateBorrowingDto) {
    const index = this.borrowings.findIndex((item) => item.id === id);
    if (index === -1) throw new NotFoundException('Borrowing not found');
    const updated = { ...this.borrowings[index], ...dto };
    this.borrowings[index] = updated;
    return updated;
  }

  remove(id: number) {
    const index = this.borrowings.findIndex((item) => item.id === id);
    if (index === -1) throw new NotFoundException('Borrowing not found');
    const [deleted] = this.borrowings.splice(index, 1);
    return deleted;
  }

  approve(id: number) {
    const item = this.findOne(id);
    item.status = 'BORROWING';
    return item;
  }

  reject(id: number) {
    const item = this.findOne(id);
    item.status = 'REJECTED';
    return item;
  }
}
