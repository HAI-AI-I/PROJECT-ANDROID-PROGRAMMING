import { BadRequestException } from '@nestjs/common';
import { BooksService } from '../books/books.service.js';
import { BorrowingsService } from './borrowings.service.js';

describe('BorrowingsService', () => {
  it('decrements availability and rejects a borrow when no copy remains', () => {
    const booksService = new BooksService();
    const service = new BorrowingsService(booksService);
    const book = booksService.findOne(1);
    const initialAvailability = book.availableQuantity;

    for (let index = 0; index < initialAvailability; index += 1) {
      service.create({
        readerId: index + 1,
        bookId: book.id,
        borrowDate: '2026-09-17',
        dueDate: '2026-10-01',
      });
    }

    expect(booksService.findOne(book.id).availableQuantity).toBe(0);
    expect(() =>
      service.create({
        readerId: 999,
        bookId: book.id,
        borrowDate: '2026-09-17',
        dueDate: '2026-10-01',
      }),
    ).toThrow(BadRequestException);
  });
});
