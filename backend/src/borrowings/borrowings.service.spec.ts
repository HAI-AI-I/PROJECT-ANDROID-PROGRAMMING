import { BadRequestException } from '@nestjs/common';
import { BooksService } from '../books/books.service.js';
import { BorrowingsService } from './borrowings.service.js';
import { PersistentStoreService } from '../persistence/persistent-store.service.js';

describe('BorrowingsService', () => {
  function createServices() {
    const collections = new Map<string, unknown[]>();
    const store = {
      getCollection: <T>(name: string, seed: T[]) => {
        const items = (collections.get(name) ?? structuredClone(seed)) as T[];
        collections.set(name, items);
        return items;
      },
      saveCollection: <T>(name: string, items: T[]) => {
        collections.set(name, structuredClone(items) as unknown[]);
      },
    } as PersistentStoreService;
    const booksService = new BooksService(store);
    const service = new BorrowingsService(booksService, store);
    return { booksService, service };
  }

  it('decrements availability and rejects a borrow when no copy remains', () => {
    const { booksService, service } = createServices();
    const book = booksService.findOne(1);
    const initialAvailability = book.availableQuantity;

    for (let index = 0; index < initialAvailability; index += 1) {
      service.create({
        readerId: index + 1,
        bookId: book.id,
        borrowDate: '2026-09-17',
        loanDays: 14,
      });
    }

    expect(booksService.findOne(book.id).availableQuantity).toBe(0);
    expect(() =>
      service.create({
        readerId: 999,
        bookId: book.id,
        borrowDate: '2026-09-17',
        loanDays: 14,
      }),
    ).toThrow(BadRequestException);
  });

  it('calculates the due date and marks expired loans overdue', () => {
    const { service } = createServices();
    const borrowDate = new Date();
    borrowDate.setUTCDate(borrowDate.getUTCDate() - 20);
    const borrowDateText = borrowDate.toISOString().slice(0, 10);

    const borrowing = service.create({
      readerId: 50,
      bookId: 3,
      borrowDate: borrowDateText,
      loanDays: 14,
    });

    const expectedDueDate = new Date(`${borrowDateText}T00:00:00.000Z`);
    expectedDueDate.setUTCDate(expectedDueDate.getUTCDate() + 14);
    expect(borrowing.dueDate).toBe(expectedDueDate.toISOString().slice(0, 10));
    expect(service.findOne(borrowing.id).status).toBe('OVERDUE');
  });
});
