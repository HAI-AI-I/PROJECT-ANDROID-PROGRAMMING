import { Injectable, NotFoundException } from '@nestjs/common';
import { CreateReturnDto } from './dto/create-return.dto.js';
import { UpdateReturnDto } from './dto/update-return.dto.js';
import { ReturnRecord } from './entities/return.entity.js';
import { BorrowingsService } from '../borrowings/borrowings.service.js';
import { BooksService } from '../books/books.service.js';
import { PersistentStoreService } from '../persistence/persistent-store.service.js';

@Injectable()
export class ReturnsService {
  constructor(
    private readonly borrowingsService: BorrowingsService,
    private readonly booksService: BooksService,
    private readonly store: PersistentStoreService,
  ) {
    const persisted = this.store.getCollection<ReturnRecord>('returns', this.returns);
    this.returns.splice(0, this.returns.length, ...persisted);
    this.nextId = Math.max(0, ...this.returns.map((item) => item.id)) + 1;
  }

  private readonly returns: ReturnRecord[] = [
    { id: 1, borrowingId: 4, returnDate: '2026-09-12', condition: 'GOOD', fine: 0, note: 'Tốt', status: 'CONFIRMED' },
  ];

  private nextId = this.returns.length + 1;

  private persist() {
    this.store.saveCollection('returns', this.returns);
  }

  findAll() {
    return [...this.returns];
  }

  findOne(id: number) {
    const record = this.returns.find((item) => item.id === id);
    if (!record) throw new NotFoundException('Return record not found');
    return record;
  }

  create(dto: CreateReturnDto) {
    const record: ReturnRecord = {
      id: this.nextId++,
      borrowingId: dto.borrowingId,
      returnDate: dto.returnDate,
      condition: dto.condition,
      fine: dto.fine,
      note: dto.note,
      status: 'PENDING',
    };
    this.returns.unshift(record);
    this.persist();
    return record;
  }

  update(id: number, dto: UpdateReturnDto) {
    const index = this.returns.findIndex((item) => item.id === id);
    if (index === -1) throw new NotFoundException('Return record not found');
    const updated = { ...this.returns[index], ...dto };
    this.returns[index] = updated;
    this.persist();
    return updated;
  }

  remove(id: number) {
    const index = this.returns.findIndex((item) => item.id === id);
    if (index === -1) throw new NotFoundException('Return record not found');
    const [deleted] = this.returns.splice(index, 1);
    this.persist();
    return deleted;
  }

  confirm(id: number) {
    const record = this.findOne(id);
    if (record.status === 'CONFIRMED') return record;
    const borrowing = this.borrowingsService.markReturned(
      record.borrowingId,
      record.returnDate,
    );
    record.status = 'CONFIRMED';
    if (record.condition !== 'LOST') {
      this.booksService.returnCopy(borrowing.bookId);
    }
    this.persist();
    return record;
  }
}
