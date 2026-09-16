import { BadRequestException } from '@nestjs/common';
import { BooksService } from '../books/books.service.js';
import { BorrowingsService } from './borrowings.service.js';
import { PersistentStoreService } from '../persistence/persistent-store.service.js';

describe('BorrowingsService', () => {
  it('decrements availability and rejects a borrow when no copy remains', () => {
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
